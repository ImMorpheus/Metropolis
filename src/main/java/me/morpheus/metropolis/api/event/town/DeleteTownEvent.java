package me.morpheus.metropolis.api.event.town;

import org.spongepowered.api.event.Cancellable;

public interface DeleteTownEvent extends TargetTownEvent {

    interface Pre extends DeleteTownEvent, Cancellable {}

    interface Post extends DeleteTownEvent {}
}
