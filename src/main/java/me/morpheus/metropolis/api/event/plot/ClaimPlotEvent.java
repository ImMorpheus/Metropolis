package me.morpheus.metropolis.api.event.plot;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface ClaimPlotEvent extends TargetPlotEvent {

    Location<World> getLocation();

    interface Pre extends ClaimPlotEvent, Cancellable {}

    interface Post extends ClaimPlotEvent {}
}
