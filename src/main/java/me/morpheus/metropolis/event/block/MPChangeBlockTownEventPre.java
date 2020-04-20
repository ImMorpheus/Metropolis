package me.morpheus.metropolis.event.block;

import me.morpheus.metropolis.api.event.block.ChangeBlockTownEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.stream.Stream;

public final class MPChangeBlockTownEventPre implements ChangeBlockTownEvent.Pre {

    private final Cause cause;
    private final List<Location<World>> locations;
    private boolean cancelled = false;

    public MPChangeBlockTownEventPre(Cause cause, List<Location<World>> locations) {
        this.cause = cause;
        this.locations = locations;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Stream<Location<World>> getLocations() {
        return this.locations.stream();
    }
}
