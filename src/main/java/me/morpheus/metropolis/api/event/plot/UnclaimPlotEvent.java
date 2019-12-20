package me.morpheus.metropolis.api.event.plot;

import org.spongepowered.api.event.Cancellable;

public interface UnclaimPlotEvent extends TargetPlotEvent {

    interface Pre extends UnclaimPlotEvent, Cancellable {}

    interface Post extends UnclaimPlotEvent {}
}
