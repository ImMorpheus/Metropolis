package me.morpheus.metropolis.event.plot;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.plot.ClaimPlotEvent;
import org.spongepowered.api.event.cause.Cause;

public final class MPClaimPlotEventPre implements ClaimPlotEvent.Pre {

    private final Cause cause;
    private final PlotData plot;
    private boolean cancelled = false;

    public MPClaimPlotEventPre(Cause cause, PlotData plot) {
        this.cause = cause;
        this.plot = plot;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public PlotData getPlot() {
        return this.plot;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
