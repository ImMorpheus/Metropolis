package me.morpheus.metropolis.api.event.entity;

import me.morpheus.metropolis.api.plot.Plot;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.entity.TargetEntityEvent;

import java.util.Optional;

public interface MoveEntityPlotEvent extends TargetEntityEvent, Cancellable {

    Optional<Plot> getFromPlot();

    Optional<Plot> getToPlot();

}
