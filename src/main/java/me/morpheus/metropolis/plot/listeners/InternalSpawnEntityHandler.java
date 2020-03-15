package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.event.entity.SpawnEntityTownEvent;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.event.entity.MPSpawnEntityTownEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

public final class InternalSpawnEntityHandler {

    private final PlotService ps;

    public InternalSpawnEntityHandler(PlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.PRE, beforeModifications = true)
    public void onSpawnEntityInit(SpawnEntityEvent event) {
        event.getEntitySnapshots();
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onSpawnEntity(SpawnEntityEvent event) {
        if (event.getEntities().stream().anyMatch(entity -> this.ps.get(entity.getLocation()).isPresent())) {
            final SpawnEntityTownEvent townEvent = new MPSpawnEntityTownEvent(event.getCause(), event.getEntities(), event.getEntitySnapshots());
            if (Sponge.getEventManager().post(townEvent)) {
                event.setCancelled(true);
            }
        }
    }

}
