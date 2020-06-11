package me.morpheus.metropolis.api.town;

import me.morpheus.metropolis.api.plot.PlotType;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.text.Text;

import java.util.Set;

public interface Upgrade extends CatalogType {

    Text getDescription();

    Set<TownType> getRequiredTownTypes();

    TownType getTarget();

    double getCost();

    short getMaxCitizens();

    short getMinCitizens();

    short getMaxPlots(PlotType type);

    short getMinPlots(PlotType type);

}
