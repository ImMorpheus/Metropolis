package me.morpheus.metropolis.event.world;

import me.morpheus.metropolis.api.event.block.ExplosionTownEvent;
import me.morpheus.metropolis.api.plot.Plot;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.List;
import java.util.stream.Stream;

public final class MPExplosionTownEventPre implements ExplosionTownEvent.Pre {

    private final Cause cause;
    private final Explosion explosion;
    private final List<Plot> plots;
    private boolean cancelled = false;

    public MPExplosionTownEventPre(Cause cause, Explosion explosion, List<Plot> plots) {
        this.cause = cause;
        this.explosion = explosion;
        this.plots = plots;
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
    public Explosion getExplosion() {
        return this.explosion;
    }

    @Override
    public Stream<Plot> getPlots() {
        return this.plots.stream();
    }
}
