package me.morpheus.metropolis.api.event.town;

import org.spongepowered.api.event.Cancellable;

public interface LeaveTownEvent extends TargetTownEvent {

    interface Pre extends LeaveTownEvent, Cancellable {}

    interface Post extends LeaveTownEvent {}
}
