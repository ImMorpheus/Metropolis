package me.morpheus.metropolis.event.plot;

import me.morpheus.metropolis.api.event.plot.UnclaimPlotEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class MPUnclaimPlotEventPre implements UnclaimPlotEvent.Pre {

    private final Cause cause;
    private final Location<World> location;
    private boolean cancelled = false;

    public MPUnclaimPlotEventPre(Cause cause, Location<World> location) {
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

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
