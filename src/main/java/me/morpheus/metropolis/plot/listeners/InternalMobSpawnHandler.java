package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.event.entity.SpawnEntityTownEvent;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.event.entity.MPSpawnEntityTownEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

public final class InternalMobSpawnHandler {

    private final PlotService ps;

    public InternalMobSpawnHandler(PlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.PRE, beforeModifications = true)
    public void onMobSpawn(SpawnEntityEvent event) {
        if (event.getEntities().stream().anyMatch(entity -> entity instanceof Monster && this.ps.get(entity.getLocation()).isPresent())) {
            SpawnEntityTownEvent townEvent = new MPSpawnEntityTownEvent(event.getCause(), event.getEntities(), event.getEntitySnapshots());
            if (Sponge.getEventManager().post(townEvent)) {
                event.setCancelled(true);
            }
        }
    }

}
