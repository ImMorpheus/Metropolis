package me.morpheus.metropolis.event.town;

import me.morpheus.metropolis.api.event.town.LeaveTownEvent;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.event.cause.Cause;

public final class MPLeaveTownEventPre implements LeaveTownEvent.Pre {

    private final Cause cause;
    private final Town town;
    private boolean cancelled = false;

    public MPLeaveTownEventPre(Cause cause, Town town) {
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

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
