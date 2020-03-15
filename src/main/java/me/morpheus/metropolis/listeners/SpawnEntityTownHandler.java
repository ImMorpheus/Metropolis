package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.entity.SpawnEntityTownEvent;
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
                final Optional<PlotData> pdOpt = ps.get(entity.getLocation());

                if (!pdOpt.isPresent()) {
                    return false;
                }

                return !pdOpt.get().mobSpawn().get();
            }
            return false;
        });
    }

}
