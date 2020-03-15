package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.entity.MobSpawnTownEvent;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.event.entity.MPMobSpawnTownEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import java.util.Optional;

public final class InternalMobSpawnHandler {

    private final PlotService ps;

    public InternalMobSpawnHandler(PlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onMobSpawn(SpawnEntityEvent event) {
        event.filterEntities(entity -> {
            if (entity instanceof Monster) {
                Optional<PlotData> pdOpt = this.ps.get(entity.getLocation());
                if (pdOpt.isPresent()) {
                    MobSpawnTownEvent mobSpawnEvent = new MPMobSpawnTownEvent(event.getCause(), entity);
                    if (Sponge.getEventManager().post(mobSpawnEvent)) {
                        return false;
                    } else if (!pdOpt.get().mobSpawn().get()) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

}
