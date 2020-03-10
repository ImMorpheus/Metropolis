package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.town.TownService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.SaveWorldEvent;

public final class SaveHandler {

    private long last = 0;

    @Listener
    public void onReload(SaveWorldEvent.Pre event) {

        long now = System.currentTimeMillis();
        if (now - this.last < 300000) {
            return;
        }
        this.last = now;

        Sponge.getServiceManager().provideUnchecked(TownService.class)
                .saveAll()
                .thenRun(() -> MPLog.getLogger().info("Towns saved"));

        Sponge.getServiceManager().provideUnchecked(PlotService.class)
                .saveAll()
                .thenRun(() -> MPLog.getLogger().info("Plots saved"));
    }
}
