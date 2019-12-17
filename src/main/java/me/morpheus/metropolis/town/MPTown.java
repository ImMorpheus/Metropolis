package me.morpheus.metropolis.town;

import com.google.common.base.MoreObjects;
import com.udojava.evalex.Expression;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.town.TownData;
import me.morpheus.metropolis.api.town.visibility.Visibilities;
import me.morpheus.metropolis.event.TownTransactionEventUpkeep;
import me.morpheus.metropolis.util.Hacks;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.data.town.TownKeys;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
import me.morpheus.metropolis.api.town.pvp.PvPOptions;
import me.morpheus.metropolis.api.town.visibility.Visibility;
import me.morpheus.metropolis.data.DataVersions;
import me.morpheus.metropolis.town.chat.TownMessageChannel;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoubleSupplier;

public class MPTown implements Town {

    private static final boolean ECONOMY = Sponge.getServiceManager().provide(EconomyService.class).isPresent();

    private final int id;
    private final Instant founded;

    private MessageChannel tc;
    private TownType type;

    private Text name;
    private Text tag;
    private PvPOption pvp;
    private Location<World> spawn;
    private Visibility visibility;

    private boolean dirty;

    private int citizens = 0;
    private int plots = 0;

    //Data
    private final Map<Class, DataManipulator> manipulators = new IdentityHashMap<>();

    MPTown(int id, TownType type, Text name, Location<World> spawn, Instant founded) {
        this.id = id;
        this.founded = founded;

        this.tc = new TownMessageChannel(this);
        this.type = type;

        this.name = name;

        final GlobalConfig global = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        final int tagLimit = global.getTownCategory().getTagMaxLength();

        final String plain = name.toPlain();
        this.tag = plain.length() <= tagLimit ? name : Text.of(plain.substring(0, tagLimit));

        this.spawn = spawn;

        this.pvp = global.getTownCategory().getDefaultsCategory().pvp();
        this.visibility = global.getTownCategory().getDefaultsCategory().visibility();

        this.dirty = true;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Instant getFounded() {
        return this.founded;
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public void setName(Text name) {
        this.name = name;
        setDirty(true);
    }

    @Override
    public TownType getType() {
        return this.type;
    }

    @Override
    public void setType(TownType type) {
        this.type = type;
        setDirty(true);
    }

    @Override
    public Text getTag() {
        return this.tag;
    }

    @Override
    public void setTag(Text tag) {
        this.tag = tag;
        setDirty(true);
    }

    @Override
    public Location<World> getSpawn() {
        return this.spawn;
    }

    @Override
    public void setSpawn(Location<World> spawn) {
        this.spawn = spawn;
        setDirty(true);
    }

    @Override
    public PvPOption getPvP() {
        if (this.pvp == PvPOptions.GRACE_PERIOD) {
            checkGracePeriod();
        }
        return this.pvp;
    }

    private void checkGracePeriod() {
        final Duration grace = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal().getTownCategory().getFoundationGracePeriod();
        if (Instant.now().isAfter(this.founded.plus(grace))) {
            this.pvp = PvPOptions.ON;
            Sponge.getServer().getBroadcastChannel().send(TextUtil.watermark(this.name, " grace period just ended"));
        }
    }

    @Override
    public void setPvP(PvPOption pvp) {
        this.pvp = pvp;
        setDirty(true);
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
        setDirty(true);
    }

    @Override
    public Optional<Account> getBank() {
        if (!MPTown.ECONOMY) {
            return Optional.empty();
        }
        final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        final Optional<Account> accountOpt = es.getOrCreateAccount(Metropolis.ID + "+" + this.id);

        if (!accountOpt.isPresent()) {
            MPLog.getLogger().error("Error while creating a bank for town {} ({})", this.name, Integer.toString(this.id));
        }

        return accountOpt;
    }

    @Override
    public BigDecimal getUpkeep() {
        if (this.type.getTaxFunction().isEmpty()) {
            return BigDecimal.ZERO;
        }
        Expression expression = new Expression(this.type.getTaxFunction());
        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            TownTransactionEventUpkeep event = new TownTransactionEventUpkeep(frame.getCurrentCause(), this);
            if (Sponge.getEventManager().post(event)) {
                return BigDecimal.ZERO;
            }
            for (Map.Entry<String, DoubleSupplier> entry : event.getMap().entrySet()) {
                expression.with(entry.getKey(), BigDecimal.valueOf(entry.getValue().getAsDouble()));
            }
        }
        return expression.eval();
    }

    @Override
    public List<Text> getTownScreen(@Nullable MessageReceiver receiver) {
        final List<Text> list = new ArrayList<>();
        list.add(Text.of(TextColors.DARK_GREEN, "Founded: ", TextColors.GREEN, this.founded.truncatedTo(ChronoUnit.SECONDS)));
        list.add(Text.of(TextColors.DARK_GREEN, "Type: ", TextColors.GREEN, this.type.getName()));
        list.add(Text.of(TextColors.DARK_GREEN, "PvP: ", TextColors.GREEN, this.pvp.getName()));
        list.add(Text.of(TextColors.DARK_GREEN, "Visibility: ", TextColors.GREEN, this.visibility.getName()));
        if (canSeeSpawn(receiver)) {
            list.add(Text.of(TextColors.DARK_GREEN, "Spawn: ", TextColors.GREEN, this.spawn.getBlockPosition(), " in ", this.spawn.getExtent().getName()));
        }
        final Optional<Account> bOpt = getBank();
        if (bOpt.isPresent()) {
            final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
            list.add(Text.of(TextColors.DARK_GREEN, "Balance: ", TextColors.GREEN, es.getDefaultCurrency().getSymbol(), bOpt.get().getBalance(es.getDefaultCurrency())));
        }
        list.add(Text.of(TextColors.DARK_GREEN, "Citizens: ", TextColors.GREEN, "[", this.citizens, "/", this.type.getMaxCitizen(), "]"));
        list.add(Text.of(TextColors.DARK_GREEN, "Plots: ", TextColors.GREEN, "[", this.plots, "/", this.type.getMaxPlot(), "]"));
        list.add(Text.of(TextColors.DARK_GREEN, "Tag: ", TextColors.GREEN, this.tag));
        final Optional<TownData> tdOpt = get(TownData.class);
        if (tdOpt.isPresent()) {
            if (tdOpt.get().description().get().isPresent()) {
                list.add(Text.of(TextColors.DARK_GREEN, "Description: ", TextColors.GREEN, tdOpt.get().description().get().get()));
            }
            if (tdOpt.get().motd().get().isPresent()) {
                list.add(Text.of(TextColors.DARK_GREEN, "Motd: ", TextColors.GREEN, tdOpt.get().motd().get().get()));
            }
        }
        //TODO fire an event
        return list;
    }

    private boolean canSeeSpawn(@Nullable MessageReceiver receiver) {
        if (receiver == null) {
            return true;
        }

        if (this.visibility == Visibilities.PUBLIC) {
            return true;
        }

        if (receiver instanceof Player) {
            final Optional<CitizenData> cdOpt = ((Player) receiver).get(CitizenData.class);
            return cdOpt.isPresent() && cdOpt.get().town().get().intValue() == this.id;
        }

        if (receiver instanceof ConsoleSource) {
            return true;
        }

        return false;
    }

    @Override
    public boolean accept(UUID user, Rank rank) {
        if (this.citizens >= this.type.getMaxCitizen()) {
            return false;
        }
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        final Optional<User> uOpt = uss.get(user);
        if (!uOpt.isPresent()) {
            return false;
        }

        final CitizenData cd = Sponge.getDataManager().getManipulatorBuilder(CitizenData.class).get().create();
        cd.set(CitizenKeys.TOWN, this.id);
        cd.set(CitizenKeys.RANK, rank);
        final DataTransactionResult result = uOpt.get().offer(cd);
        if (!result.isSuccessful()) {
            return false;
        }
        this.citizens++;
        setDirty(true);
        return true;
    }

    @Override
    public boolean kick(UUID user) {
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        final Optional<User> uOpt = uss.get(user);
        if (!uOpt.isPresent()) {
            return false;
        }
        final DataTransactionResult result = uOpt.get().remove(CitizenData.class);
        if (!result.isSuccessful()) {
            return false;
        }
        this.citizens--;
        setDirty(true);
        return true;
    }

    @Override
    public boolean claim(Location<World> location, PlotType type, @Nullable Text name) {
        if (this.plots >= this.type.getMaxPlot()) {
            return false;
        }
        if (type == PlotTypes.OUTPOST) {
            Optional<OutpostData> odOpt = getOrCreate(OutpostData.class);
            if (!odOpt.isPresent()) {
                return false;
            }
            final String n = name == null ? "null" : name.toPlain();
            Map<String, Location<World>> map = odOpt.get().outposts().get();
            Location<World> l = map.putIfAbsent(n, location);
            if (l != null) {
                return false;
            }
            odOpt.get().set(TownKeys.OUTPOSTS, map);
            offer(odOpt.get());
        }
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final PlotData pd = Sponge.getDataManager().getManipulatorBuilder(PlotData.class).get().create();
        pd.set(PlotKeys.TOWN, this.id);
        pd.set(PlotKeys.TYPE, type);
        if (name != null) {
            pd.set(PlotKeys.NAME, name);
        }
        final Optional<PlotData> pdOpt = ps.claim(location, pd);
        if (pdOpt.isPresent()) {
            return false;
        }
        this.plots++;
        setDirty(true);
        return true;
    }

    @Override
    public boolean unclaim(Location<World> location) {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<PlotData> pdOpt = ps.unclaim(location);
        if (!pdOpt.isPresent()) {
            return false;
        }
        if (pdOpt.get().type().get() == PlotTypes.OUTPOST) {
            Optional<OutpostData> odOpt = get(OutpostData.class);
            odOpt.ifPresent(outpostData -> outpostData.outposts().remove(pdOpt.get().name().get().toPlain()));
        }
        this.plots--;
        setDirty(true);
        return true;
    }

    @Override
    public void disband() {
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        ts.delete(this.id);

        final Text disband = TextUtil.watermark(TextColors.RED, "Your town has been disbanded");
        for (Player player : Sponge.getServer().getOnlinePlayers()) {
            player.get(CitizenKeys.TOWN).ifPresent(town -> {
                if (town.intValue() == this.id) {
                    player.sendMessage(disband);
                    player.remove(CitizenData.class);
                }
            });
        }


        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        ps.unclaim(pd -> pd.town().get().intValue() == this.id);
    }

    @Override
    public void sendMessage(Text message) {
        this.tc.send(message);
    }

    @Override
    public MessageChannel getMessageChannel() {
        return this.tc;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
        this.tc = channel;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Town && this.id == ((Town) obj).getId());
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ID", this.id)
                .add("Name", this.name)
                .add("Spawn", this.spawn)
                .toString();
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public int getCitizens() {
        return this.citizens;
    }

    public void setCitizens(int citizens) {
        this.citizens = citizens;
    }

    public int getPlots() {
        return this.plots;
    }

    public void setPlots(int plots) {
        this.plots = plots;
    }

    // Data

    @Override
    public boolean validateRawData(DataView container) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public void setRawData(DataView container) throws InvalidDataException {
        throw new UnsupportedOperationException(); //TODO

    }

    @Override
    public int getContentVersion() {
        return DataVersions.Town.CURRENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer data = DataContainer.createNew()
                .set(DataQuery.of("id"), this.id)
                .set(DataQuery.of("founded"), this.founded)
                .set(DataQuery.of("type"), this.type)
                .set(DataQuery.of("name"), this.name)
                .set(DataQuery.of("tag"), this.tag)
                .set(DataQuery.of("pvp"), this.pvp)
                .set(DataQuery.of("spawn"), Hacks.toContainer(this.spawn))
                .set(DataQuery.of("visibility"), this.visibility)
                .set(DataQuery.of("plots"), this.citizens)
                .set(DataQuery.of("citizens"), this.plots);

        final Collection<DataManipulator> manipulators = this.manipulators.values();
        if (!manipulators.isEmpty()) {
            data.set(Hacks.DATA_MANIPULATORS, Hacks.serializeManipulatorList((Iterable<DataManipulator<?, ?>>) (Object) manipulators));
        }

        return data;
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        return Optional.empty(); //TODO
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return Collections.emptyList(); //TODO
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        return (Optional<T>) (Object) Optional.ofNullable(this.manipulators.get(containerClass));
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        final Optional<T> get = get(containerClass);
        if (get.isPresent()) {
            return get;
        }
        Optional<DataManipulatorBuilder<?, ?>> builder = Sponge.getDataManager().getManipulatorBuilder((Class) (Object) containerClass);
        if (!builder.isPresent()) {
            MPLog.getLogger().error("A DataManipulatorBuilder is not registered for the manipulator class: {}", containerClass.getName());
            return Optional.empty();
        }

        final T manipulator = (T) builder.get().create();
        return manipulator.fill(this).map(customManipulator -> (T) customManipulator);
    }

    @Override
    public boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
        return true;
    }

    @Override
    public <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value) {
        for (DataManipulator<?, ?> manipulator : this.manipulators.values()) {
            if (manipulator.supports(key)) {
                final DataTransactionResult.Builder builder = DataTransactionResult.builder();
                builder.replace(((Value) manipulator.getValue((Key) key).get()).asImmutable());
                manipulator.set(key, value);
                builder.success(((Value) manipulator.getValue((Key) key).get()).asImmutable());
                setDirty(true);
                return builder.result(DataTransactionResult.Type.SUCCESS).build();
            }
        }
        return DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult offer(DataManipulator<?, ?> valueContainer, MergeFunction function) {
        final Class clazz = Hacks.getRequiredClass((DataManipulatorBuilder) (Object) Sponge.getDataManager().getManipulatorBuilder((Class) valueContainer.getClass()).get());

        DataManipulator<?, ?> existingManipulator = this.manipulators.get(clazz);
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        final DataManipulator<?, ?> newManipulator = function.merge(existingManipulator, (DataManipulator) valueContainer.copy());
        if (existingManipulator != null) {
            builder.replace(existingManipulator.getValues());
        }
        this.manipulators.put(clazz, valueContainer);
        setDirty(true);
        return builder.success(newManipulator.getValues())
                .result(DataTransactionResult.Type.SUCCESS)
                .build();
    }

    @Override
    public DataTransactionResult remove(Class<? extends DataManipulator<?, ?>> containerClass) {
        DataManipulator<?, ?> result = this.manipulators.remove(containerClass);
        setDirty(true);
        return DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult remove(Key<?> key) {
        final Iterator<DataManipulator> iterator = this.manipulators.values().iterator();
        while (iterator.hasNext()) {
            final DataManipulator<?, ?> manipulator = iterator.next();
            if (manipulator.getKeys().size() == 1 && manipulator.supports(key)) {
                iterator.remove();
                setDirty(true);
                return DataTransactionResult.builder()
                        .replace(manipulator.getValues())
                        .result(DataTransactionResult.Type.SUCCESS)
                        .build();
            }
        }
        return DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult undo(DataTransactionResult result) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Collection<DataManipulator<?, ?>> getContainers() {
        return (Collection) Collections.unmodifiableCollection(this.manipulators.values());
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        for (DataManipulator manipulator : this.manipulators.values()) {
            if (manipulator.supports(key)) {
                return manipulator.get(key);
            }
        }
        return Optional.empty();
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        for (DataManipulator manipulator : this.manipulators.values()) {
            if (manipulator.supports(key)) {
                return manipulator.getValue(key);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean supports(Key<?> key) {
        return true;
    }

    @Override
    public DataHolder copy() {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public Set<Key<?>> getKeys() {
        Set<Key<?>> keys = new HashSet<>();
        for (DataManipulator manipulator : this.manipulators.values()) {
            keys.addAll(manipulator.getKeys());
        }
        return keys;
    }

    @Override
    public Set<ImmutableValue<?>> getValues() {
        Set<ImmutableValue<?>> keys = new HashSet<>();
        for (DataManipulator manipulator : this.manipulators.values()) {
            keys.addAll(manipulator.getValues());
        }
        return keys;
    }
}
