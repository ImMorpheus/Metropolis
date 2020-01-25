package me.morpheus.metropolis.api.plot;

import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@CatalogedBy(PlotTypes.class)
public interface PlotType extends CatalogType {

    boolean canClaim(Town t, Location<World> location);

}
