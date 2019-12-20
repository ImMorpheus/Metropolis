package me.morpheus.metropolis.api.event.town;

import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.event.Event;

public interface TargetTownEvent extends Event {

    Town getTargetTown();

}
