package me.morpheus.metropolis;

import com.google.inject.Inject;
import me.morpheus.metropolis.api.command.CommandDispatcher;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.EconomyCategory;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.data.citizen.ImmutableCitizenData;
import me.morpheus.metropolis.api.data.plot.ImmutablePlotData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.data.town.ImmutableTownData;
import me.morpheus.metropolis.api.data.town.TownData;
import me.morpheus.metropolis.api.data.town.TownKeys;
import me.morpheus.metropolis.api.data.town.economy.ImmutableTaxData;
import me.morpheus.metropolis.api.data.town.economy.TaxData;
import me.morpheus.metropolis.api.data.town.outpost.ImmutableOutpostData;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.health.Incident;
import me.morpheus.metropolis.api.health.IncidentService;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.invitation.InvitationService;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
import me.morpheus.metropolis.api.town.visibility.Visibility;
import me.morpheus.metropolis.api.util.MPTypeTokens;
import me.morpheus.metropolis.commands.admin.AdminDispatcher;
import me.morpheus.metropolis.commands.town.TownDispatcher;
import me.morpheus.metropolis.config.ConfigUtil;
import me.morpheus.metropolis.config.SimpleConfigService;
import me.morpheus.metropolis.configurate.serialize.DurationSerializer;
import me.morpheus.metropolis.custom.CustomResourceLoaderRegistryModule;
import me.morpheus.metropolis.data.citizen.ImmutableMPCitizenData;
import me.morpheus.metropolis.data.citizen.MPCitizenData;
import me.morpheus.metropolis.data.citizen.MPCitizenDataBuilder;
import me.morpheus.metropolis.data.plot.ImmutableMPPlotData;
import me.morpheus.metropolis.data.plot.MPPlotData;
import me.morpheus.metropolis.data.plot.MPPlotDataBuilder;
import me.morpheus.metropolis.data.town.ImmutableMPTownData;
import me.morpheus.metropolis.data.town.MPTownData;
import me.morpheus.metropolis.data.town.MPTownDataBuilder;
import me.morpheus.metropolis.data.town.economy.ImmutableMPTaxData;
import me.morpheus.metropolis.data.town.economy.MPTaxData;
import me.morpheus.metropolis.data.town.economy.MPTaxDataBuilder;
import me.morpheus.metropolis.data.town.outpost.ImmutableMPOutpostData;
import me.morpheus.metropolis.data.town.outpost.MPOutpostData;
import me.morpheus.metropolis.data.town.outpost.MPOutpostDataBuilder;
import me.morpheus.metropolis.error.MPGenericErrors;
import me.morpheus.metropolis.error.WarningLoginHandler;
import me.morpheus.metropolis.flag.FlagRegistryModule;
import me.morpheus.metropolis.health.MPIncident;
import me.morpheus.metropolis.health.MPreconditions;
import me.morpheus.metropolis.health.SimpleIncidentService;
import me.morpheus.metropolis.listeners.ChangeBlockHandler;
import me.morpheus.metropolis.listeners.ChatHandler;
import me.morpheus.metropolis.listeners.DamageEntityHandler;
import me.morpheus.metropolis.listeners.Debug;
import me.morpheus.metropolis.listeners.ExplosionHandler;
import me.morpheus.metropolis.listeners.InteractHandler;
import me.morpheus.metropolis.listeners.LoginHandler;
import me.morpheus.metropolis.listeners.MoveEntityHandler;
import me.morpheus.metropolis.listeners.NotifyHandler;
import me.morpheus.metropolis.listeners.ReloadHandler;
import me.morpheus.metropolis.listeners.SaveHandler;
import me.morpheus.metropolis.plot.PlotTypeRegistryModule;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.rank.RankRegistryModule;
import me.morpheus.metropolis.town.SimpleTownService;
import me.morpheus.metropolis.town.economy.AsyncTaxCollectionTask;
import me.morpheus.metropolis.town.invitation.SimpleInvitationService;
import me.morpheus.metropolis.town.pvp.PvPOptionRegistryModule;
import me.morpheus.metropolis.town.type.TownTypeRegistryModule;
import me.morpheus.metropolis.town.visibility.VisibilityRegistryModule;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Plugin(id = Metropolis.ID, name = Metropolis.NAME, version = Metropolis.VERSION, description = Metropolis.DESCRIPTION)
public class Metropolis {

    public static final String ID = "metropolis";
    public static final String NAME = "Metropolis";
    public static final String VERSION = "0.0.1";
    public static final String DESCRIPTION = "Towny-like plugin";

    @Inject private PluginContainer container;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        TypeSerializers.getDefaultSerializers()
                .registerType(MPTypeTokens.DURATION_TOKEN, new DurationSerializer());

        Sponge.getRegistry().registerModule(CustomResourceLoader.class, new CustomResourceLoaderRegistryModule());
        Sponge.getRegistry().registerModule(PlotType.class, new PlotTypeRegistryModule());
        Sponge.getRegistry().registerModule(TownType.class, new TownTypeRegistryModule());
        Sponge.getRegistry().registerModule(Flag.class, new FlagRegistryModule());
        Sponge.getRegistry().registerModule(Rank.class, new RankRegistryModule());
        Sponge.getRegistry().registerModule(PvPOption.class, new PvPOptionRegistryModule());
        Sponge.getRegistry().registerModule(Visibility.class, new VisibilityRegistryModule());

        Sponge.getServiceManager().setProvider(this.container, TownService.class, new SimpleTownService());
        Sponge.getServiceManager().setProvider(this.container, PlotService.class, new SimplePlotService());
        Sponge.getServiceManager().setProvider(this.container, InvitationService.class, new SimpleInvitationService());
        Sponge.getServiceManager().setProvider(this.container, IncidentService.class, new SimpleIncidentService());
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        try {
            registerConfigService();
        } catch (Exception e) {
            Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                    .create(new MPIncident(MPGenericErrors.config(), e));
        }
        registerCommands();

        Sponge.getEventManager().registerListeners(this.container, new ChangeBlockHandler());
        Sponge.getEventManager().registerListeners(this.container, new DamageEntityHandler());
        Sponge.getEventManager().registerListeners(this.container, new ExplosionHandler());
        Sponge.getEventManager().registerListeners(this.container, new InteractHandler());
        Sponge.getEventManager().registerListeners(this.container, new LoginHandler());
        Sponge.getEventManager().registerListeners(this.container, new NotifyHandler());
        Sponge.getEventManager().registerListeners(this.container, new ReloadHandler());
        Sponge.getEventManager().registerListeners(this.container, new ChatHandler());
        Sponge.getEventManager().registerListeners(this.container, new SaveHandler());

        final GlobalConfig g = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();

        if (g.isPlotMessageEnabled()) {
            Sponge.getEventManager().registerListeners(this.container, new MoveEntityHandler());
        }

        Sponge.getEventManager().registerListeners(this.container, new Debug()); //TODO
    }

    @Listener
    public void onPostInit(GameAboutToStartServerEvent event) {
        final IncidentService is = Sponge.getServiceManager().provideUnchecked(IncidentService.class);
        Incident i = MPreconditions.checkDefaultRanks();
        if (i != null) {
            is.create(i);
        }
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        Sponge.getServiceManager().provideUnchecked(PlotService.class)
                .loadAll()
                .thenRun(() -> MPLog.getLogger().info("Plots loaded"));

        Sponge.getServiceManager().provideUnchecked(TownService.class)
                .loadAll()
                .thenRun(() -> MPLog.getLogger().info("Towns loaded"));
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        final IncidentService is = Sponge.getServiceManager().provideUnchecked(IncidentService.class);
        final Collection<Incident> incidents = is.getAll();

        if (!incidents.isEmpty()) {
            Sponge.getEventManager().unregisterPluginListeners(this.container);
            Sponge.getEventManager().registerListeners(this.container, new WarningLoginHandler());

            MPLog.getLogger().error("{} failed to start", Metropolis.NAME);

            for (Incident incident : is.getAll()) {
                Sponge.getServer().getConsole().sendMessage(incident.getError());
                incident.getThrowable().ifPresent(s -> MPLog.getLogger().error("Error ", s));
            }
            return;
        }

        final EconomyCategory ec = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal().getEconomyCategory();
        if (ec.isEnabled()) {
            final LocalDateTime now = LocalDateTime.now();
            final long delay = now.until(now.withHour(ec.getTaxHour()).plusDays(1L), ChronoUnit.NANOS);

            Sponge.getScheduler().createTaskBuilder()
                    .async()
                    .delay(delay, TimeUnit.NANOSECONDS)
                    .interval(1L, TimeUnit.DAYS)
                    .name(Metropolis.ID + "+async-tax")
                    .execute(AsyncTaxCollectionTask::run)
                    .submit(this.container);
        }
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        Sponge.getServiceManager().provideUnchecked(TownService.class)
                .saveAll()
                .thenRun(() -> MPLog.getLogger().info("Towns saved"));

        Sponge.getServiceManager().provideUnchecked(PlotService.class)
                .saveAll()
                .thenRun(() -> MPLog.getLogger().info("Plots saved"));

        for (CustomResourceLoader loader : Sponge.getRegistry().getAllOf(CustomResourceLoader.class)) {
            loader.save();
        }
    }

    @Listener
    public void onKeyRegistration(GameRegistryEvent.Register<Key<?>> event) {
        event.register(CitizenKeys.TOWN);
        event.register(CitizenKeys.RANK);
        event.register(CitizenKeys.FRIENDS);
        event.register(CitizenKeys.JOINED);
        event.register(CitizenKeys.CHAT);

        event.register(PlotKeys.TOWN);
        event.register(PlotKeys.NAME);
        event.register(PlotKeys.OWNER);
        event.register(PlotKeys.PRICE);
        event.register(PlotKeys.RENT);
        event.register(PlotKeys.FORSALE);
        event.register(PlotKeys.TYPE);

        event.register(TownKeys.DESCRIPTION);
        event.register(TownKeys.MOTD);
        event.register(TownKeys.OUTPOSTS);
        event.register(TownKeys.TAX);
    }

    @Listener
    public void onDataRegistration(GameRegistryEvent.Register<DataRegistration<?, ?>> event) {
        DataRegistration.builder()
                .dataClass(CitizenData.class)
                .immutableClass(ImmutableCitizenData.class)
                .dataImplementation(MPCitizenData.class)
                .immutableImplementation(ImmutableMPCitizenData.class)
                .builder(new MPCitizenDataBuilder())
                .name("Citizen Data")
                .id("citizen")
                .build();

        DataRegistration.builder()
                .dataClass(PlotData.class)
                .immutableClass(ImmutablePlotData.class)
                .dataImplementation(MPPlotData.class)
                .immutableImplementation(ImmutableMPPlotData.class)
                .builder(new MPPlotDataBuilder())
                .name("Plot Data")
                .id("plot")
                .build();

        DataRegistration.builder()
                .dataClass(TaxData.class)
                .immutableClass(ImmutableTaxData.class)
                .dataImplementation(MPTaxData.class)
                .immutableImplementation(ImmutableMPTaxData.class)
                .builder(new MPTaxDataBuilder())
                .name("Tax Data")
                .id("tax")
                .build();

        DataRegistration.builder()
                .dataClass(OutpostData.class)
                .immutableClass(ImmutableOutpostData.class)
                .dataImplementation(MPOutpostData.class)
                .immutableImplementation(ImmutableMPOutpostData.class)
                .builder(new MPOutpostDataBuilder())
                .name("Outpost Data")
                .id("outpost")
                .build();

        DataRegistration.builder()
                .dataClass(TownData.class)
                .immutableClass(ImmutableTownData.class)
                .dataImplementation(MPTownData.class)
                .immutableImplementation(ImmutableMPTownData.class)
                .builder(new MPTownDataBuilder())
                .name("Town Data")
                .id("town")
                .build();
    }

    private void registerCommands() {
        final CommandDispatcher town = new TownDispatcher();
        town.registerDefaults();
        Sponge.getCommandManager().register(this.container, town, "town", "t");

        final CommandDispatcher mpadmin = new AdminDispatcher();
        mpadmin.registerDefaults();
        Sponge.getCommandManager().register(this.container, mpadmin, "mpadmin");
    }

    private void registerConfigService() throws IOException, ExecutionException, InterruptedException {
        final ConfigService cs = new SimpleConfigService();
        if (Files.notExists(ConfigUtil.CONF)) {
            Files.createFile(ConfigUtil.CONF);
            Sponge.getServiceManager().setProvider(this.container, ConfigService.class, cs);
            cs.save()
                    .thenRun(() -> MPLog.getLogger().info("Config saved"))
                    .exceptionally(throwable -> {
                        Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                                .create(new MPIncident(MPGenericErrors.config(), throwable));
                        return null;
                    });
        } else {
            cs.reload().get();
            MPLog.getLogger().info("Config loaded");
            Sponge.getServiceManager().setProvider(this.container, ConfigService.class, cs);
        }
    }
}
