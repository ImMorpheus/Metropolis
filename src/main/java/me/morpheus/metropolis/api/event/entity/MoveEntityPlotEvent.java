package me.morpheus.metropolis.api.event.entity;

import me.morpheus.metropolis.api.data.plot.PlotData;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.TargetEntityEvent;

import java.util.Optional;

public interface MoveEntityPlotEvent extends TargetEntityEvent, Cancellable {

    Optional<PlotData> getFromPlot();

    Optional<PlotData> getToPlot();

}
