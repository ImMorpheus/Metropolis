package me.morpheus.metropolis.event.plot;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.plot.ClaimPlotEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class MPClaimPlotEventPre implements ClaimPlotEvent.Pre {

    private final Cause cause;
    private final PlotData plot;
    private final Location<World> location;
    private boolean cancelled = false;

    public MPClaimPlotEventPre(Cause cause, PlotData plot, Location<World> location) {
        this.cause = cause;
        this.plot = plot;
        this.location = location;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public PlotData getPlot() {
        return this.plot;
    }

    @Override
    public Location<World> getLocation() {
        return this.location;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
