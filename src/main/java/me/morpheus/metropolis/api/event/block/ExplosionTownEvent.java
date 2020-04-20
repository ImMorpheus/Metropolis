package me.morpheus.metropolis.api.event.block;

import me.morpheus.metropolis.api.plot.Plot;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.stream.Stream;

public interface ExplosionTownEvent extends Event {

    Explosion getExplosion();

    Stream<Plot> getPlots();

    interface Pre extends ExplosionTownEvent, Cancellable {}
}
