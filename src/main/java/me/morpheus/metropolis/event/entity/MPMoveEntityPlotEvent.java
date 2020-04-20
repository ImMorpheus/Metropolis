package me.morpheus.metropolis.event.entity;

import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import me.morpheus.metropolis.api.plot.Plot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MPMoveEntityPlotEvent implements MoveEntityPlotEvent {

    private final Cause cause;
    private final Entity entity;
    private final Plot from;
    private final Plot to;
    private boolean cancelled = false;

    public MPMoveEntityPlotEvent(Cause cause, Entity entity, @Nullable Plot from, @Nullable Plot to) {
        this.cause = cause;
        this.entity = entity;
        this.from = from;
        this.to = to;
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

    @Override
    public Optional<Plot> getFromPlot() {
        return Optional.ofNullable(this.from);
    }

    @Override
    public Optional<Plot> getToPlot() {
        return Optional.ofNullable(this.to);
    }
}
