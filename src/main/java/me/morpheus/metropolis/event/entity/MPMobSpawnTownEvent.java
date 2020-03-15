package me.morpheus.metropolis.event.entity;

import me.morpheus.metropolis.api.event.entity.MobSpawnTownEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;

import java.util.List;

public final class MPMobSpawnTownEvent implements MobSpawnTownEvent {

    private final Cause cause;
    private final Entity entity;
    private boolean cancelled = false;

    public MPMobSpawnTownEvent(Cause cause, Entity entity) {
        this.cause = cause;
        this.entity = entity;
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
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Entity getTargetEntity() {
        return this.entity;
    }
}
