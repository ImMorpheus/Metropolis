package me.morpheus.metropolis.event.plot;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.plot.UnclaimPlotEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class MPUnclaimPlotEventPost implements UnclaimPlotEvent.Post {

    private final Cause cause;
    private final Location<World> location;

    public MPUnclaimPlotEventPost(Cause cause, Location<World> location) {
        this.cause = cause;
        this.location = location;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Location<World> getLocation() {
        return this.location;
    }
}
