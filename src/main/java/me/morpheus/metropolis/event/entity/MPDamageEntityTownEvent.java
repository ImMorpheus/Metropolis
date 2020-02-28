package me.morpheus.metropolis.event.entity;

import me.morpheus.metropolis.api.event.entity.DamageEntityTownEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;

public final class MPDamageEntityTownEvent implements DamageEntityTownEvent {

    private final Cause cause;
    private final Entity entity;
    private boolean cancelled = false;

    public MPDamageEntityTownEvent(Cause cause, Entity entity) {
        this.cause = cause;
        this.entity = entity;
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
    public Entity getTargetEntity() {
        return this.entity;
    }
}
