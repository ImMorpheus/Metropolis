package me.morpheus.metropolis.api.event.plot;

import org.spongepowered.api.event.Cancellable;

public interface ClaimPlotEvent extends TargetPlotEvent {

    interface Pre extends ClaimPlotEvent, Cancellable {}

    interface Post extends ClaimPlotEvent {}
}
