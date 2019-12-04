package me.morpheus.metropolis.town.listeners;

import me.morpheus.metropolis.api.event.TownTransactionEvent;
import me.morpheus.metropolis.town.MPTown;
import org.spongepowered.api.event.Listener;

public class InternalTownHandler {

    @Listener(beforeModifications = true)
    public void onUpkeep(TownTransactionEvent.Upkeep event) {
        final MPTown t = (MPTown) event.getTargetTown();
        event.addSupplier("c", t::getCitizens);
        event.addSupplier("p", t::getPlots);
    }
}
