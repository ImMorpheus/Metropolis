package me.morpheus.metropolis.event.plot;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.plot.ClaimPlotEvent;
import org.spongepowered.api.event.cause.Cause;

public final class MPClaimPlotEventPost implements ClaimPlotEvent.Post {

    private final Cause cause;
    private final PlotData plot;

    public MPClaimPlotEventPost(Cause cause, PlotData plot) {
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
}
