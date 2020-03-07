package me.morpheus.metropolis.town.upgrade;

import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.Upgrade;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Set;

@ConfigSerializable
class MPUpgrade implements Upgrade {

    @Setting private String id;
    @Setting private String name;
    @Setting(value = "required-towntypes") private Set<TownType> requiredTownTypes;
    @Setting private TownType target;
    @Setting private double cost;
    @Setting(value = "required-citizens") private int requiredCitizens;
    @Setting(value = "required-plots") private int requiredPlots;

    public MPUpgrade(String id, String name, Set<TownType> requiredTownTypes, TownType target,
                     double cost, int requiredCitizens, int requiredPlots) {
        this.id = id;
        this.name = name;
        this.requiredTownTypes = requiredTownTypes;
        this.target = target;
        this.cost = cost;
        this.requiredCitizens = requiredCitizens;
        this.requiredPlots = requiredPlots;
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
    public int getRequiredCitizens() {
        return this.requiredCitizens;
    }

    @Override
    public int getRequiredPlots() {
        return this.requiredPlots;
    }
}
