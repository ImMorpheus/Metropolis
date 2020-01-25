package me.morpheus.metropolis.plot;

import com.google.common.base.MoreObjects;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.BiPredicate;
import java.util.function.ToDoubleFunction;

class MPPlotType implements PlotType {

    private final String id;
    private final String name;
    private final BiPredicate<Town, Location<World>> canClaim;

    MPPlotType(String id, String name, BiPredicate<Town, Location<World>> canClaim) {
        this.id = id;
        this.name = name;
        this.canClaim = canClaim;
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
    public boolean canClaim(Town t, Location<World> location) {
        return this.canClaim.test(t, location);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Id", this.id)
                .add("Name", this.name)
                .toString();
    }
}
