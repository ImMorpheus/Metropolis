package me.morpheus.metropolis.event.world;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.block.ExplosionTownEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.List;
import java.util.stream.Stream;

public final class MPExplosionTownEventPre implements ExplosionTownEvent.Pre {

    private final Cause cause;
    private final Explosion explosion;
    private final List<PlotData> plots;
    private boolean cancelled = false;

    public MPExplosionTownEventPre(Cause cause, Explosion explosion, List<PlotData> plots) {
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
    public Stream<PlotData> getPlots() {
        return this.plots.stream();
    }
}
