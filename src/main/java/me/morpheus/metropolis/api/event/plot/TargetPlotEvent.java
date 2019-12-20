package me.morpheus.metropolis.api.event.plot;

import me.morpheus.metropolis.api.data.plot.PlotData;
import org.spongepowered.api.event.Event;

public interface TargetPlotEvent extends Event {

    PlotData getPlot();

}
