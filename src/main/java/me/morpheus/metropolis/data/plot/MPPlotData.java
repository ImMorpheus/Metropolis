package me.morpheus.metropolis.data.plot;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import me.morpheus.metropolis.api.data.plot.ImmutablePlotData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.data.DataVersions;
import me.morpheus.metropolis.util.Hacks;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MPPlotData extends AbstractData<PlotData, ImmutablePlotData> implements PlotData {

    private int town;
    @Nullable private Text name;
    @Nullable private UUID owner;
    private double price;
    private double rent;
    private boolean forSale;
    private PlotType type;
    private boolean mobSpawn;
    @Nullable private Reference2ByteMap<Flag> permissions;

    MPPlotData() {
        this(Integer.MIN_VALUE, null, null, 0, 0, false, PlotTypes.PLOT, false, null);
    }

    MPPlotData(int town, @Nullable Text name, @Nullable UUID owner, double price, double rent, boolean forSale, PlotType type, boolean mobSpawn, @Nullable Reference2ByteMap<Flag> permissions) {
        this.town = town;
        this.name = name;
        this.owner = owner;
        this.price = price;
        this.rent = rent;
        this.forSale = forSale;
        this.type = type;
        this.mobSpawn = mobSpawn;
        this.permissions = permissions;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(PlotKeys.TOWN, this::town);
        registerKeyValue(PlotKeys.NAME, this::name);
        registerKeyValue(PlotKeys.OWNER, this::owner);
        registerKeyValue(PlotKeys.PRICE, this::price);
        registerKeyValue(PlotKeys.RENT, this::rent);
        registerKeyValue(PlotKeys.FORSALE, this::forSale);
        registerKeyValue(PlotKeys.TYPE, this::type);
        registerKeyValue(PlotKeys.MOBSPAWN, this::mobSpawn);

        registerFieldGetter(PlotKeys.TOWN, this::getTown);
        registerFieldGetter(PlotKeys.NAME, this::getName);
        registerFieldGetter(PlotKeys.OWNER, this::getOwner);
        registerFieldGetter(PlotKeys.PRICE, this::getPrice);
        registerFieldGetter(PlotKeys.RENT, this::getRent);
        registerFieldGetter(PlotKeys.FORSALE, this::isForSale);
        registerFieldGetter(PlotKeys.TYPE, this::getType);
        registerFieldGetter(PlotKeys.MOBSPAWN, this::hasMobSpawn);

        // Only on mutable implementation
        registerFieldSetter(PlotKeys.TOWN, this::setTown);
        registerFieldSetter(PlotKeys.NAME, this::setName);
        registerFieldSetter(PlotKeys.OWNER, this::setOwner);
        registerFieldSetter(PlotKeys.PRICE, this::setPrice);
        registerFieldSetter(PlotKeys.RENT, this::setRent);
        registerFieldSetter(PlotKeys.FORSALE, this::setForSale);
        registerFieldSetter(PlotKeys.TYPE, this::setType);
        registerFieldSetter(PlotKeys.MOBSPAWN, this::setMobSpawn);
    }

    @Override
    public Optional<PlotData> fill(DataHolder dataHolder, MergeFunction overlap) {
        PlotData merged = overlap.merge(this, dataHolder.get(PlotData.class).orElse(null));
        this.town = merged.town().get();
        this.name = merged.name().get();
        this.owner = merged.owner().get().orElse(null);
        this.price = merged.price().get();
        this.rent = merged.rent().get();
        this.forSale = merged.forSale().get();
        this.type = merged.type().get();
        this.mobSpawn = merged.mobSpawn().get();

        return Optional.of(this);
    }

    @Override
    public Optional<PlotData> from(DataContainer container) {
        Optional<Integer> town = container.getInt(PlotKeys.TOWN.getQuery());
        if (!town.isPresent()) {
            return Optional.empty();
        }
        Optional<Text> name = container.getSerializable(PlotKeys.NAME.getQuery(), Text.class);
        Optional<UUID> owner = container.getObject(PlotKeys.OWNER.getQuery(), UUID.class);
        Optional<Double> price = container.getDouble(PlotKeys.PRICE.getQuery());
        Optional<Double> rent = container.getDouble(PlotKeys.RENT.getQuery());
        Optional<Boolean> forSale = container.getBoolean(PlotKeys.FORSALE.getQuery());
        Optional<PlotType> type = container.getCatalogType(PlotKeys.TYPE.getQuery(), PlotType.class);
        Optional<Boolean> mobSpawn = container.getBoolean(PlotKeys.MOBSPAWN.getQuery());
        Reference2ByteMap<Flag> permissions = Hacks.permissionsFrom(container);

        this.town = town.get();
        this.name = name.orElse(null);
        this.owner = owner.orElse(null);
        this.price = price.orElse(0.0);
        this.rent = rent.orElse(0.0);
        this.forSale = forSale.orElse(false);
        this.type = type.orElse(PlotTypes.PLOT);
        this.mobSpawn = mobSpawn.orElse(false);

        if (!permissions.isEmpty()) {
            this.permissions = permissions;
            this.permissions.defaultReturnValue(Byte.MIN_VALUE);
        }

        return Optional.of(this);
    }

    @Override
    public PlotData copy() {
        return new MPPlotData(this.town, this.name, this.owner, this.price, this.rent, this.forSale, this.type, this.mobSpawn, this.permissions);
    }

    @Override
    public ImmutablePlotData asImmutable() {
        return new ImmutableMPPlotData(this.town, this.name, this.owner, this.price, this.rent, this.forSale, this.type, this.mobSpawn, this.permissions);
    }

    @Override
    public int getContentVersion() {
        return DataVersions.PlotData.CURRENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = super.toContainer();
        container.set(PlotKeys.TOWN.getQuery(), this.town);
        if (this.name != null && !this.name.isEmpty()) {
            container.set(PlotKeys.NAME.getQuery(), this.name);
        }
        if (this.owner != null) {
            container.set(PlotKeys.OWNER.getQuery(), this.owner);
        }
        container.set(PlotKeys.PRICE.getQuery(), this.price);
        container.set(PlotKeys.RENT.getQuery(), this.rent);
        container.set(PlotKeys.FORSALE.getQuery(), this.forSale);
        container.set(PlotKeys.TYPE.getQuery(), this.type);
        container.set(PlotKeys.MOBSPAWN.getQuery(), this.mobSpawn);
        if (this.permissions != null && !this.permissions.isEmpty()) {
            container.set(DataQuery.of("permissions"), Hacks.toContainer(this.permissions));
        }

        return container;
    }

    @Override
    public Value<Integer> town() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.TOWN, this.town);
    }

    @Override
    public Value<Text> name() {
        if (this.name == null) {
            this.name = Text.of();
        }
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.NAME, this.name);
    }

    @Override
    public OptionalValue<UUID> owner() {
        return Sponge.getRegistry().getValueFactory().createOptionalValue(PlotKeys.OWNER, this.owner);
    }

    @Override
    public Value<Double> price() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.PRICE, this.price);
    }

    @Override
    public Value<Double> rent() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.RENT, this.rent);
    }

    @Override
    public Value<Boolean> forSale() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.FORSALE, this.forSale);
    }

    @Override
    public Value<PlotType> type() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.TYPE, this.type);
    }

    @Override
    public Value<Boolean> mobSpawn() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.MOBSPAWN, this.mobSpawn);
    }

    @Override
    public byte getPermission(Flag flag) {
        if (this.permissions == null) {
            return Byte.MIN_VALUE;
        }
        return this.permissions.getByte(flag);
    }

    @Override
    public void addPermission(Flag flag, byte value) {
        if (this.permissions == null) {
            this.permissions = new Reference2ByteOpenHashMap<>();
            this.permissions.defaultReturnValue(Byte.MIN_VALUE);
        }
        this.permissions.put(flag, value);
    }

    private int getTown() {
        return this.town;
    }

    private void setTown(int town) {
        this.town = town;
    }

    @Nullable
    public Text getName() {
        return this.name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    @Nullable
    private UUID getOwner() {
        return this.owner;
    }

    private void setOwner(Optional<UUID> owner) {
        this.owner = owner.orElse(null);
    }

    private double getPrice() {
        return this.price;
    }

    private void setPrice(double price) {
        this.price = price;
    }

    private double getRent() {
        return this.rent;
    }

    private void setRent(double rent) {
        this.rent = rent;
    }

    private boolean isForSale() {
        return this.forSale;
    }

    private void setForSale(boolean forSale) {
        this.forSale = forSale;
    }

    private PlotType getType() {
        return this.type;
    }

    private void setType(PlotType type) {
        this.type = type;
    }

    private boolean hasMobSpawn() {
        return this.mobSpawn;
    }

    private void setMobSpawn(boolean mobSpawn) {
        this.mobSpawn = mobSpawn;
    }
}
