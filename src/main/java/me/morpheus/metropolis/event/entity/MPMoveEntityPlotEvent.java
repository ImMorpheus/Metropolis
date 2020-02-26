package me.morpheus.metropolis.event.entity;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MPMoveEntityPlotEvent implements MoveEntityPlotEvent {

    private final Cause cause;
    private final Entity entity;
    private final PlotData from;
    private final PlotData to;
    private boolean cancelled = false;

    public MPMoveEntityPlotEvent(Cause cause, Entity entity, @Nullable PlotData from, @Nullable PlotData to) {
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
    public Optional<PlotData> getFromPlot() {
        return Optional.ofNullable(this.from);
    }

    @Override
    public Optional<PlotData> getToPlot() {
        return Optional.ofNullable(this.to);
    }
}
