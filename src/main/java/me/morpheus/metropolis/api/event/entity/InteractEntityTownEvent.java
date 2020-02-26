package me.morpheus.metropolis.api.event.entity;

import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;

public interface InteractEntityTownEvent extends InteractEvent, TargetEntityEvent {

    interface Primary extends InteractEntityTownEvent {}

    interface Secondary extends InteractEntityTownEvent {}
}
