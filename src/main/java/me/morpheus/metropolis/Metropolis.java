package me.morpheus.metropolis;

import com.google.inject.Inject;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.EconomyCategory;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.data.citizen.ImmutableCitizenData;
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
import me.morpheus.metropolis.api.town.Upgrade;
import me.morpheus.metropolis.api.town.invitation.InvitationService;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
import me.morpheus.metropolis.api.town.visibility.Visibility;
import me.morpheus.metropolis.api.util.MPTypeTokens;
import me.morpheus.metropolis.commands.admin.ForceTaxCommand;
import me.morpheus.metropolis.commands.admin.SaveCommand;
import me.morpheus.metropolis.commands.admin.town.JoinCommand;
import me.morpheus.metropolis.commands.town.ChatCommand;
import me.morpheus.metropolis.commands.town.ClaimCommand;
import me.morpheus.metropolis.commands.town.DepositCommand;
import me.morpheus.metropolis.commands.town.DisbandCommand;
import me.morpheus.metropolis.commands.town.InfoCommand;
import me.morpheus.metropolis.commands.town.InviteCommand;
import me.morpheus.metropolis.commands.town.KickCommand;
import me.morpheus.metropolis.commands.town.LeaveCommand;
import me.morpheus.metropolis.commands.town.ListCommand;
import me.morpheus.metropolis.commands.town.NewCommand;
import me.morpheus.metropolis.commands.town.OutpostCommand;
import me.morpheus.metropolis.commands.town.PricesCommand;
import me.morpheus.metropolis.commands.town.SpawnCommand;
import me.morpheus.metropolis.commands.town.UnclaimCommand;
import me.morpheus.metropolis.commands.town.UpgradeCommand;
import me.morpheus.metropolis.commands.town.WithdrawCommand;
import me.morpheus.metropolis.commands.town.citizen.OnlineCommand;
import me.morpheus.metropolis.commands.town.friend.AddCommand;
import me.morpheus.metropolis.commands.town.friend.ClearCommand;
import me.morpheus.metropolis.commands.town.friend.RemoveCommand;
import me.morpheus.metropolis.commands.town.plot.BuyCommand;
import me.morpheus.metropolis.commands.town.plot.DisownCommand;
import me.morpheus.metropolis.commands.town.plot.NameCommand;
import me.morpheus.metropolis.commands.town.plot.RentCommand;
import me.morpheus.metropolis.commands.town.plot.SellCommand;
import me.morpheus.metropolis.commands.town.plot.perm.SetCommand;
import me.morpheus.metropolis.commands.town.plot.set.MobSpawnCommand;
import me.morpheus.metropolis.commands.town.set.DescriptionCommand;
import me.morpheus.metropolis.commands.town.set.MotdCommand;
import me.morpheus.metropolis.commands.town.set.PvPCommand;
import me.morpheus.metropolis.commands.town.set.TagCommand;
import me.morpheus.metropolis.commands.town.set.TaxCommand;
import me.morpheus.metropolis.commands.town.set.VisibilityCommand;
import me.morpheus.metropolis.config.SimpleConfigService;
import me.morpheus.metropolis.configurate.serialize.DurationSerializer;
import me.morpheus.metropolis.custom.CustomResourceLoaderRegistryModule;
import me.morpheus.metropolis.data.citizen.ImmutableMPCitizenData;
import me.morpheus.metropolis.data.citizen.MPCitizenData;
import me.morpheus.metropolis.data.citizen.MPCitizenDataBuilder;
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
import me.morpheus.metropolis.flag.FlagRegistryModule;
import me.morpheus.metropolis.health.MPIncident;
import me.morpheus.metropolis.health.MPreconditions;
import me.morpheus.metropolis.health.SimpleIncidentService;
import me.morpheus.metropolis.listeners.ChangeBlockTownHandler;
import me.morpheus.metropolis.listeners.ChatHandler;
import me.morpheus.metropolis.listeners.DamageEntityTownHandler;
import me.morpheus.metropolis.listeners.InteractTownHandler;
import me.morpheus.metropolis.listeners.MoveEntityTownHandler;
import me.morpheus.metropolis.listeners.ReloadHandler;
import me.morpheus.metropolis.listeners.SaveHandler;
import me.morpheus.metropolis.listeners.SpawnEntityTownHandler;
import me.morpheus.metropolis.listeners.debug.ChangeBlockDebugHandler;
import me.morpheus.metropolis.listeners.debug.DamageEntityDebugHandler;
import me.morpheus.metropolis.plot.PlotTypeRegistryModule;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.rank.RankRegistryModule;
import me.morpheus.metropolis.task.DailyTask;
import me.morpheus.metropolis.town.SimpleTownService;
import me.morpheus.metropolis.town.invitation.SimpleInvitationService;
import me.morpheus.metropolis.town.pvp.PvPOptionRegistryModule;
import me.morpheus.metropolis.town.type.TownTypeRegistryModule;
import me.morpheus.metropolis.town.upgrade.UpgradeRegistryModule;
import me.morpheus.metropolis.town.visibility.VisibilityRegistryModule;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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
        Sponge.getRegistry().registerModule(Upgrade.class, new UpgradeRegistryModule());

        Sponge.getServiceManager().setProvider(this.container, TownService.class, new SimpleTownService());
        Sponge.getServiceManager().setProvider(this.container, PlotService.class, new SimplePlotService());
        Sponge.getServiceManager().setProvider(this.container, InvitationService.class, new SimpleInvitationService());
        Sponge.getServiceManager().setProvider(this.container, IncidentService.class, new SimpleIncidentService());
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        try {
            registerConfigService();

            Sponge.getServiceManager().provideUnchecked(ConfigService.class)
                    .reload()
                    .get();
        } catch (Exception e) {
            Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                    .create(new MPIncident(MPGenericErrors.config(), e));
            return;
        }
        registerCommands();

        Sponge.getServiceManager().provideUnchecked(TownService.class).registerListeners();
        Sponge.getServiceManager().provideUnchecked(PlotService.class).registerListeners();

        Sponge.getEventManager().registerListeners(this.container, new ChangeBlockTownHandler());
        Sponge.getEventManager().registerListeners(this.container, new DamageEntityTownHandler());
        // Sponge.getEventManager().registerListeners(this.container, new ExplosionTownHandler()); //TODO
        Sponge.getEventManager().registerListeners(this.container, new InteractTownHandler());
        Sponge.getEventManager().registerListeners(this.container, new SpawnEntityTownHandler());
        Sponge.getEventManager().registerListeners(this.container, new ReloadHandler());
        Sponge.getEventManager().registerListeners(this.container, new SaveHandler());

        final GlobalConfig g = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();

        if (g.getChatCategory().isEnabled()) {
            Sponge.getEventManager().registerListeners(this.container, new ChatHandler());
        }

        if (g.isPlotMessageEnabled()) {
            Sponge.getEventManager().registerListeners(this.container, new MoveEntityTownHandler());
        }

        if (Boolean.parseBoolean(System.getProperty(Metropolis.ID + ".debugListeners", null))) {
            registerDebugCommands();

            Sponge.getEventManager().registerListeners(this.container, new ChangeBlockDebugHandler());
            Sponge.getEventManager().registerListeners(this.container, new DamageEntityDebugHandler());
        }
    }

    @Listener
    public void onPostInit(GameAboutToStartServerEvent event) {
        MPreconditions.checkDefaultRanks();
        MPreconditions.checkEconomyIntegration();
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        final IncidentService is = Sponge.getServiceManager().provideUnchecked(IncidentService.class);

        if (!is.isEmpty()) {
            return;
        }

        Sponge.getServiceManager().provideUnchecked(PlotService.class)
                .loadAll()
                .thenRun(() -> MPLog.getLogger().info("Plots loaded"))
                .exceptionally(e -> {
                    is.create(new MPIncident(Text.of("Error while loading the plots"), e));
                    return null;
                });

        Sponge.getServiceManager().provideUnchecked(TownService.class)
                .loadAll()
                .thenRun(() -> MPLog.getLogger().info("Towns loaded"))
                .exceptionally(e -> {
                    is.create(new MPIncident(Text.of("Error while loading the towns"), e));
                    return null;
                });
    }

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        final IncidentService is = Sponge.getServiceManager().provideUnchecked(IncidentService.class);

        if (!is.isEmpty()) {
            is.setSafeMode();

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
            final long delay = now.until(now.withHour(12).plusDays(1L), ChronoUnit.NANOS);

            Sponge.getScheduler().createTaskBuilder()
                    .delay(delay, TimeUnit.NANOSECONDS)
                    .interval(1L, TimeUnit.DAYS)
                    .name(Metropolis.ID + "+daily")
                    .execute(DailyTask::run)
                    .submit(this.container);
        }
    }

    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        final IncidentService is = Sponge.getServiceManager().provideUnchecked(IncidentService.class);

        if (!is.isEmpty()) {
            return;
        }

        Sponge.getServiceManager().provideUnchecked(TownService.class)
                .saveAll()
                .join();
        MPLog.getLogger().info("Towns saved");

        Sponge.getServiceManager().provideUnchecked(PlotService.class)
                .saveAll()
                .join();
        MPLog.getLogger().info("Plots saved");

        for (CustomResourceLoader<?> loader : Sponge.getRegistry().getAllOf(CustomResourceLoader.class)) {
            loader.save();
        }
    }

    @Listener
    public void onKeyRegistration(GameRegistryEvent.Register<Key<?>> event) {
        event.register(CitizenKeys.TOWN);
        event.register(CitizenKeys.RANK);
        event.register(CitizenKeys.FRIENDS);
        event.register(CitizenKeys.JOINED);

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
        final CommandSpec.Builder t = CommandSpec.builder();
        {
            t.child(new ChatCommand(), "chat")
                    .child(new ClaimCommand(), "claim")
                    .child(new DepositCommand(), "deposit")
                    .child(new DisbandCommand(), "disband")
                    .child(new InfoCommand(), "info")
                    .child(new InviteCommand(), "invite")
                    .child(new KickCommand(), "kick")
                    .child(new LeaveCommand(), "leave")
                    .child(new ListCommand(), "list")
                    .child(new NewCommand(), "new", "create")
                    .child(new OutpostCommand(), "outpost")
                    .child(new PricesCommand(), "prices")
                    .child(new SpawnCommand(), "spawn")
                    .child(new UnclaimCommand(), "unclaim")
                    .child(new UpgradeCommand(), "upgrade")
                    .child(new WithdrawCommand(), "withdraw");
        }

        {
            final CommandSpec citizen = CommandSpec.builder()
                    .child(new me.morpheus.metropolis.commands.town.citizen.InfoCommand(), "info")
                    .child(new me.morpheus.metropolis.commands.town.citizen.ListCommand(), "list")
                    .child(new OnlineCommand(), "online")
                    .build();
            t.child(citizen, "citizen");
        }

        {
            final CommandSpec friend = CommandSpec.builder()
                    .child(new AddCommand(), "add")
                    .child(new RemoveCommand(), "remove")
                    .child(new me.morpheus.metropolis.commands.town.friend.ListCommand(), "list")
                    .child(new ClearCommand(), "clear")
                    .build();
            t.child(friend, "friend");
        }

        {
            final CommandSpec perm = CommandSpec.builder()
                    .child(new me.morpheus.metropolis.commands.town.plot.perm.RemoveCommand(), "remove")
                    .child(new SetCommand(), "set")
                    .child(new me.morpheus.metropolis.commands.town.plot.perm.ListCommand(), "list")
                    .build();

            final CommandSpec set = CommandSpec.builder()
                    .child(new MobSpawnCommand(), "mobspawn")
                    .build();

            final CommandSpec plot = CommandSpec.builder()
                    .child(new me.morpheus.metropolis.commands.town.plot.InfoCommand(), "info")
                    .child(new BuyCommand(), "buy")
                    .child(new DisownCommand(), "disown")
                    .child(new SellCommand(), "sell")
                    .child(new RentCommand(), "rent")
                    .child(new NameCommand(), "name")
                    .child(perm, "perm")
                    .child(set, "set")
                    .build();
            t.child(plot, "plot");
        }

        {
            final CommandSpec rank = CommandSpec.builder()
                    .child(new me.morpheus.metropolis.commands.town.rank.SetCommand(), "set")
                    .build();
            t.child(rank, "rank");
        }

        {
            final CommandSpec set = CommandSpec.builder()
                    .child(new DescriptionCommand(), "description", "desc")
                    .child(new MotdCommand(), "motd")
                    .child(new me.morpheus.metropolis.commands.town.set.NameCommand(), "name")
                    .child(new VisibilityCommand(), "visibility")
                    .child(new PvPCommand(), "pvp")
                    .child(new me.morpheus.metropolis.commands.town.set.SpawnCommand(), "spawn")
                    .child(new TagCommand(), "tag")
                    .child(new TaxCommand(), "tax")
                    .build();
            t.child(set, "set");
        }

        Sponge.getCommandManager().register(this.container, t.build(), "town", "t");

        final CommandSpec.Builder mpadmin = CommandSpec.builder()
                .child(new SaveCommand(), "save")
                .child(new ForceTaxCommand(), "forcetax");

        {
            final CommandSpec town = CommandSpec.builder()
                    .child(new JoinCommand(), "join")
                    .child(new me.morpheus.metropolis.commands.admin.town.LeaveCommand(), "leave")
                    .build();
            mpadmin.child(town, "town");
        }

        {
            final CommandSpec plot = CommandSpec.builder()
                    .child(new me.morpheus.metropolis.commands.admin.plot.DisownCommand(), "disown")
                    .build();
            mpadmin.child(plot, "plot");
        }

        Sponge.getCommandManager().register(this.container, mpadmin.build(), "mpadmin");

        final CommandSpec.Builder outpost = CommandSpec.builder();

        {
            final CommandSpec set = CommandSpec.builder()
                    .child(new me.morpheus.metropolis.commands.outpost.set.NameCommand(), "name")
                    .build();
            outpost.child(set, "set");
        }

        Sponge.getCommandManager().register(this.container, outpost.build(), "outpost");

        Sponge.getServiceManager().provideUnchecked(PlotService.class).registerCommands();
    }

    private void registerDebugCommands() {
        final CommandSpec changeblock = CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.bool(Text.of("toggle"))))
                .executor((src, args) -> {
                    final Optional<Boolean> toggleOpt = args.getOne(Text.of("toggle"));
                    ChangeBlockDebugHandler.enabled = toggleOpt.isPresent() || ChangeBlockDebugHandler.cancelled != Tristate.UNDEFINED;
                    ChangeBlockDebugHandler.cancelled = toggleOpt.map(Tristate::fromBoolean).orElse(Tristate.UNDEFINED);
                    return CommandResult.success();
                })
                .permission(Metropolis.ID + ".commands.debug")
                .build();
        final CommandSpec damage = CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.bool(Text.of("toggle"))))
                .executor((src, args) -> {
                    final Optional<Boolean> toggleOpt = args.getOne(Text.of("toggle"));
                    DamageEntityDebugHandler.enabled = toggleOpt.isPresent() || DamageEntityDebugHandler.cancelled != Tristate.UNDEFINED;
                    DamageEntityDebugHandler.cancelled = toggleOpt.map(Tristate::fromBoolean).orElse(Tristate.UNDEFINED);
                    return CommandResult.success();
                })
                .permission(Metropolis.ID + ".commands.debug")
                .build();
        final CommandSpec debug = CommandSpec.builder()
                .child(changeblock, "changeblock")
                .child(damage, "damage")
                .permission(Metropolis.ID + ".commands.debug")
                .build();
        Sponge.getCommandManager().register(this.container, debug, "mpdebug");
    }

    private void registerConfigService() throws IOException, ObjectMappingException {
        final SimpleConfigService cs = new SimpleConfigService();
        cs.populate();
        Sponge.getServiceManager().setProvider(this.container, ConfigService.class, cs);
    }
}
