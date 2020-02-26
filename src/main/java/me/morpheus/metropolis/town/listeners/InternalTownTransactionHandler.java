package me.morpheus.metropolis.town.listeners;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.event.town.TownTransactionEvent;
import me.morpheus.metropolis.town.MPTown;
import org.spongepowered.api.event.Listener;

public class InternalTownTransactionHandler {

    @Listener(beforeModifications = true)
    public void onUpkeep(TownTransactionEvent.Upkeep event) {
        final MPTown t = (MPTown) event.getTargetTown();
        event.addSupplier("c", t::getCitizens);
        for (Reference2IntMap.Entry<PlotType> entry : t.getPlots().reference2IntEntrySet()) {
            event.addSupplier(entry.getKey().getId(), entry::getIntValue);
        }
    }
}
