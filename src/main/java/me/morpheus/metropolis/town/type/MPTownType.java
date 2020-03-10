package me.morpheus.metropolis.town.type;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ShortMap;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.town.TownType;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
class MPTownType implements TownType {

    @Setting private String id;
    @Setting private String name;
    @Setting(value = "tax-function") private String taxFunction;
    @Setting(value = "spawn-price") private double spawnPrice;
    @Setting(value = "claim-prices") private Reference2DoubleMap<PlotType> prices;
    @Setting(value = "max-citizens") private short maxCitizens;
    @Setting(value = "max-plots") private Reference2ShortMap<PlotType> maxPlots;

    MPTownType(String id, String name, String taxFunction, double spawnPrice, short maxCitizens,
               Reference2DoubleMap<PlotType> prices, Reference2ShortMap<PlotType> maxPlots) {
        this.id = id;
        this.name = name;
        this.taxFunction = taxFunction;
        this.spawnPrice = spawnPrice;
        this.maxCitizens = maxCitizens;
        this.prices = prices;
        this.prices.defaultReturnValue(Double.MAX_VALUE);
        this.maxPlots = maxPlots;
        this.maxPlots.defaultReturnValue((short) 0);
    }

    private MPTownType() {
        this.id = "dummy";
        this.name = "DUMMY";
        this.taxFunction = "0";
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getTaxFunction() {
        return this.taxFunction;
    }

    @Override
    public double getSpawnPrice() {
        return this.spawnPrice;
    }

    @Override
    public double getClaimPrice(PlotType type) {
        return this.prices.getDouble(type);
    }

    public Reference2DoubleMap<PlotType> getPrices() {
        return this.prices;
    }

    @Override
    public short getMaxCitizens() {
        return this.maxCitizens;
    }

    @Override
    public short getMaxPlots(PlotType type) {
        return this.maxPlots.getShort(type);
    }

    public Reference2ShortMap<PlotType> getMaxPlots() {
        return this.maxPlots;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Id", this.id)
                .add("Name", this.name)
                .toString();
    }
}
