package me.morpheus.metropolis.api.event.block;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.stream.Stream;

public interface ChangeBlockTownEvent extends Event, Cancellable {

    interface Pre extends ChangeBlockTownEvent {

        Stream<Location<World>> getLocations();
    }

    interface Break extends ChangeBlockTownEvent {

        Stream<Transaction<BlockSnapshot>> getTransactions();
    }

    interface Place extends ChangeBlockTownEvent {

        Stream<Transaction<BlockSnapshot>> getTransactions();
    }
}
