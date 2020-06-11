package me.morpheus.metropolis.town.upgrade;

import it.unimi.dsi.fastutil.objects.Reference2ShortMap;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.Upgrade;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;

import java.util.Set;

@ConfigSerializable
class MPUpgrade implements Upgrade {

    @Setting private String id;
    @Setting private String name;
    @Setting private Text description;
    @Setting(value = "required-towntypes") private Set<TownType> requiredTownTypes;
    @Setting private TownType target;
    @Setting private double cost;
    @Setting(value = "max-citizens") private short maxCitizens;
    @Setting(value = "min-citizens") private short minCitizens;
    @Setting(value = "max-plots") private Reference2ShortMap<PlotType> maxPlots;
    @Setting(value = "min-plots") private Reference2ShortMap<PlotType> minPlots;

    public MPUpgrade(String id, String name, Text description, Set<TownType> requiredTownTypes, TownType target,
                     double cost, short maxCitizens, short minCitizens,
                     Reference2ShortMap<PlotType> maxPlots, Reference2ShortMap<PlotType> minPlots) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredTownTypes = requiredTownTypes;
        this.target = target;
        this.cost = cost;
        this.maxCitizens = maxCitizens;
        this.minCitizens = minCitizens;
        this.maxPlots = maxPlots;
        this.minPlots = minPlots;
    }

    private MPUpgrade() {
        this.id = "dummy";
        this.name = "DUMMY";
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
    public Text getDescription() {
        return this.description;
    }

    @Override
    public Set<TownType> getRequiredTownTypes() {
        return this.requiredTownTypes;
    }

    @Override
    public TownType getTarget() {
        return this.target;
    }

    @Override
    public double getCost() {
        return this.cost;
    }

    @Override
    public short getMaxCitizens() {
        return this.maxCitizens;
    }

    @Override
    public short getMinCitizens() {
        return this.minCitizens;
    }

    public Reference2ShortMap<PlotType> getMaxPlots() {
        return this.maxPlots;
    }

    public Reference2ShortMap<PlotType> getMinPlots() {
        return this.minPlots;
    }

    @Override
    public short getMaxPlots(PlotType type) {
        return this.maxPlots.getShort(type);
    }

    @Override
    public short getMinPlots(PlotType type) {
        return this.minPlots.getShort(type);
    }
}
