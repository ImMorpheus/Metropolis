package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.event.block.ExplosionTownEvent;
import me.morpheus.metropolis.api.flag.Flags;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.util.EventUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.explosive.DetonateExplosiveEvent;
import org.spongepowered.api.event.world.ExplosionEvent;

import java.util.Optional;

public final class ExplosionTownHandler {

    @Listener(beforeModifications = true)
    public void onExplosionPre(ExplosionTownEvent.Pre event) {
        final Optional<User> ownerOpt = event.getContext().get(EventContextKeys.OWNER);
        if (!ownerOpt.isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (event.getPlots().anyMatch(pd -> pd != null && !EventUtil.hasPermission(ownerOpt.get(), pd, Flags.EXPLOSION))) {
            event.setCancelled(true);
        }
    }

    @Listener(beforeModifications = true)
    public void onDetonateExplosive(DetonateExplosiveEvent event) {
//
//        final Optional<User> ownerOpt = event.getContext().get(EventContextKeys.OWNER);
//        if (!ownerOpt.isPresent()) {
//            event.setCancelled(true);
//            return;
//        }
//
//        if (this.ps.get(event.getOriginalExplosion()).anyMatch(pd -> pd != null && !EventUtil.hasPermission(ownerOpt.get(), pd, Flags.EXPLOSION))) {
//            event.setCancelled(true);
//        }
    }

}
