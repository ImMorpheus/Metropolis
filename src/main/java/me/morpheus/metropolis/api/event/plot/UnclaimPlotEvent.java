package me.morpheus.metropolis.api.event.plot;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface UnclaimPlotEvent extends Event {

    Location<World> getLocation();

    interface Pre extends UnclaimPlotEvent, Cancellable {}

    interface Post extends UnclaimPlotEvent {}
}
