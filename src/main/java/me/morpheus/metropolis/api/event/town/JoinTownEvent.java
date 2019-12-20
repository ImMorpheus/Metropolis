package me.morpheus.metropolis.api.event.town;

import org.spongepowered.api.event.Cancellable;

public interface JoinTownEvent extends TargetTownEvent {

    interface Pre extends JoinTownEvent, Cancellable {}

    interface Post extends JoinTownEvent {}
}
