package me.morpheus.metropolis.event.entity;

import me.morpheus.metropolis.api.event.entity.SpawnEntityTownEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.cause.Cause;

import java.util.List;
import java.util.stream.Stream;

public final class MPSpawnEntityTownEvent implements SpawnEntityTownEvent {

    private final Cause cause;
    private final List<Entity> entities;
    private final List<EntitySnapshot> entitySnapshots;
    private boolean cancelled = false;

    public MPSpawnEntityTownEvent(Cause cause, List<Entity> entities, List<EntitySnapshot> entitySnapshots) {
        this.cause = cause;
        this.entities = entities;
        this.entitySnapshots = entitySnapshots;
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
    public List<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public Stream<EntitySnapshot> getEntitySnapshots() {
        return this.entitySnapshots.stream();
    }
}
