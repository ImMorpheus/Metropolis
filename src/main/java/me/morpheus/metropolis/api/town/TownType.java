package me.morpheus.metropolis.api.town;

import me.morpheus.metropolis.api.plot.PlotType;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(TownTypes.class)
public interface TownType extends CatalogType {

    String getTaxFunction();

    double getSpawnPrice();

    double getClaimPrice(PlotType type);

    short getMaxCitizens();

    short getMaxPlots(PlotType type);

}
