package me.morpheus.metropolis.event.town;

import me.morpheus.metropolis.api.event.town.JoinTownEvent;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.event.cause.Cause;

public final class MPJoinTownEventPost implements JoinTownEvent.Post {

    private final Cause cause;
    private final Town town;

    public MPJoinTownEventPost(Cause cause, Town town) {
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
