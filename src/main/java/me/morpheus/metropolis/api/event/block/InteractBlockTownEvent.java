package me.morpheus.metropolis.api.event.block;

import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.TargetBlockEvent;

public interface InteractBlockTownEvent extends InteractEvent, TargetBlockEvent {

    interface Primary extends InteractBlockTownEvent {}

    interface Secondary extends InteractBlockTownEvent {}
}
