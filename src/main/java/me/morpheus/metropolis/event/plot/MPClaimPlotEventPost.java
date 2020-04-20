package me.morpheus.metropolis.event.plot;

import me.morpheus.metropolis.api.event.plot.ClaimPlotEvent;
import me.morpheus.metropolis.api.plot.Plot;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class MPClaimPlotEventPost implements ClaimPlotEvent.Post {

    private final Cause cause;
    private final Plot plot;
    private final Location<World> location;

    public MPClaimPlotEventPost(Cause cause, Plot plot, Location<World> location) {
        this.cause = cause;
        this.plot = plot;
        this.location = location;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Plot getPlot() {
        return this.plot;
    }

    @Override
    public Location<World> getLocation() {
        return this.location;
    }
}
