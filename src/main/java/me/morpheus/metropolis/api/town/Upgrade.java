package me.morpheus.metropolis.api.town;

import org.spongepowered.api.CatalogType;

import java.util.Set;

public interface Upgrade extends CatalogType {

    Set<TownType> getRequiredTownTypes();

    TownType getTarget();

    double getCost();

    short getRequiredCitizens();

    short getRequiredPlots();

}
