package me.morpheus.metropolis.event.town;

import me.morpheus.metropolis.api.event.town.LeaveTownEvent;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.event.cause.Cause;

public final class MPLeaveTownEventPost implements LeaveTownEvent.Post {

    private final Cause cause;
    private final Town town;

    public MPLeaveTownEventPost(Cause cause, Town town) {
        this.cause = cause;
        this.town = town;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Town getTargetTown() {
        return this.town;
    }
}
