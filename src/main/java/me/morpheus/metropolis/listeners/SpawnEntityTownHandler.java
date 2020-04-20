package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.event.entity.SpawnEntityTownEvent;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.event.Listener;

import java.util.Optional;

public final class SpawnEntityTownHandler {

    @Listener(beforeModifications = true)
    public void onSpawnEntity(SpawnEntityTownEvent event) {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);

        event.getEntities().removeIf(entity -> {
            if (entity instanceof Monster) {
                final Optional<Plot> plotOpt = ps.get(entity.getLocation());

                if (!plotOpt.isPresent()) {
                    return false;
                }

                return !plotOpt.get().hasMobSpawn();
            }
            return false;
        });
    }

}
