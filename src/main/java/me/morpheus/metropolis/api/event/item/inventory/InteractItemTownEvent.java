package me.morpheus.metropolis.api.event.item.inventory;

import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public interface InteractItemTownEvent extends InteractEvent {

    ItemStackSnapshot getItemStack();

    interface Primary extends InteractItemTownEvent {}

    interface Secondary extends InteractItemTownEvent {}
}
