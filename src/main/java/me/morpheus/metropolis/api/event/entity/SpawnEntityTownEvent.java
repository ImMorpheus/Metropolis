package me.morpheus.metropolis.api.event.entity;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import java.util.List;
import java.util.stream.Stream;

/**
 * An event called when an entity spawns in a claimed plot.
 */
public interface SpawnEntityTownEvent extends Event, Cancellable {

    List<Entity> getEntities();

    Stream<EntitySnapshot> getEntitySnapshots();
}
