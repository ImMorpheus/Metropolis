package me.morpheus.metropolis.event.entity;

import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import me.morpheus.metropolis.api.plot.Plot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MPMoveEntityPlotEvent implements MoveEntityPlotEvent {

    private final Cause cause;
    private final MessageFormatter formatter;
    private final Text originalMessage;
    private final Entity entity;
    private final Plot from;
    private final Plot to;
    private boolean cancelled = false;
    private boolean messageCancelled = false;

    public MPMoveEntityPlotEvent(Cause cause, MessageFormatter formatter, Text message, Entity entity, @Nullable Plot from, @Nullable Plot to) {
        this.cause = cause;
        this.formatter = formatter;
        this.originalMessage = message;
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

    @Override
    public Text getOriginalMessage() {
        return this.originalMessage;
    }

    @Override
    public boolean isMessageCancelled() {
        return this.messageCancelled;
    }

    @Override
    public void setMessageCancelled(boolean cancelled) {
        this.messageCancelled = cancelled;
    }

    @Override
    public MessageFormatter getFormatter() {
        return this.formatter;
    }
}
