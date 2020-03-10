package me.morpheus.metropolis.data.plot;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import me.morpheus.metropolis.api.data.plot.ImmutablePlotData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.data.DataVersions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.UUID;

public class ImmutableMPPlotData extends AbstractImmutableData<ImmutablePlotData, PlotData> implements ImmutablePlotData {

    private final int town;
    @Nullable private final Text name;
    @Nullable private final UUID owner;
    private final double price;
    private final double rent;
    private final boolean forSale;
    private final PlotType type;
    @Nullable private final Reference2ByteMap<Flag> permissions;

    ImmutableMPPlotData(int town, @Nullable Text name, @Nullable UUID owner, double price, double rent, boolean forSale, PlotType type, @Nullable Reference2ByteMap<Flag> permissions) {
        this.town = town;
        this.name = name;
        this.owner = owner;
        this.price = price;
        this.rent = rent;
        this.forSale = forSale;
        this.type = type;
        this.permissions = permissions;
        registerGetters();
    }

    @Override
    protected void registerGetters() {
        registerKeyValue(PlotKeys.TOWN, this::town);
        registerKeyValue(PlotKeys.NAME, this::name);
        registerKeyValue(PlotKeys.OWNER, this::owner);
        registerKeyValue(PlotKeys.PRICE, this::price);
        registerKeyValue(PlotKeys.RENT, this::rent);
        registerKeyValue(PlotKeys.FORSALE, this::forSale);
        registerKeyValue(PlotKeys.TYPE, this::type);

        registerFieldGetter(PlotKeys.TOWN, this::getTown);
        registerFieldGetter(PlotKeys.NAME, this::getName);
        registerFieldGetter(PlotKeys.OWNER, this::getOwner);
        registerFieldGetter(PlotKeys.PRICE, this::getPrice);
        registerFieldGetter(PlotKeys.RENT, this::getRent);
        registerFieldGetter(PlotKeys.FORSALE, this::isForSale);
        registerFieldGetter(PlotKeys.TYPE, this::getType);
    }

    @Override
    public PlotData asMutable() {
        return new MPPlotData(this.town, this.name, this.owner, this.price, this.rent, this.forSale, this.type, this.permissions);
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
        if (this.permissions != null && !this.permissions.isEmpty()) {
            container.set(DataQuery.of("permissions"), this.permissions);
        }

        return container;
    }

    @Override
    public ImmutableValue<Integer> town() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.TOWN, this.town).asImmutable();
    }

    @Override
    public ImmutableValue<Text> name() {
        if (this.name == null) {
            return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.NAME, Text.of()).asImmutable();
        }
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.NAME, this.name).asImmutable();
    }

    @Override
    public ImmutableOptionalValue<UUID> owner() {
        return Sponge.getRegistry().getValueFactory().createOptionalValue(PlotKeys.OWNER, this.owner).asImmutable();
    }

    @Override
    public ImmutableValue<Double> price() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.PRICE, this.price).asImmutable();
    }

    @Override
    public ImmutableValue<Double> rent() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.RENT, this.rent).asImmutable();
    }

    @Override
    public ImmutableValue<Boolean> forSale() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.FORSALE, this.forSale).asImmutable();
    }

    @Override
    public ImmutableValue<PlotType> type() {
        return Sponge.getRegistry().getValueFactory().createValue(PlotKeys.TYPE, this.type).asImmutable();
    }

    @Override
    public byte getPermission(Flag flag) {
        if (this.permissions == null) {
            return Byte.MIN_VALUE;
        }
        return this.permissions.getByte(flag);
    }

    private int getTown() {
        return this.town;
    }

    @Nullable
    public Text getName() {
        return this.name;
    }

    @Nullable
    private UUID getOwner() {
        return this.owner;
    }

    private double getPrice() {
        return this.price;
    }

    private double getRent() {
        return this.rent;
    }

    private boolean isForSale() {
        return this.forSale;
    }

    private PlotType getType() {
        return this.type;
    }
}
