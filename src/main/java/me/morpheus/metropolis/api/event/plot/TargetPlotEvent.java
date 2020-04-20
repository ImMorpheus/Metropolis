package me.morpheus.metropolis.api.event.plot;

import me.morpheus.metropolis.api.plot.Plot;
import org.spongepowered.api.event.Event;

public interface TargetPlotEvent extends Event {

    Plot getPlot();

}
