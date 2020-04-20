package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.event.entity.AttackEntityTownEvent;
import me.morpheus.metropolis.api.event.entity.DamageEntityTownEvent;
import me.morpheus.metropolis.api.flag.Flags;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.EventUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public final class DamageEntityTownHandler {

    @Listener(beforeModifications = true)
    public void onDamage(DamageEntityTownEvent event) {
        final Entity target = event.getTargetEntity();

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(target.getLocation());

        if (!plotOpt.isPresent()) {
            return;
        }

        if (target instanceof Hostile) {
            return;
        }

        final Object source = EventUtil.getDamageSource(event);

        if (!(source instanceof Player)) {
            return;
        }

        if (target instanceof Player) {
            final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
            final Town t = ts.get(plotOpt.get().getTown()).get();
            if (!t.getPvP().canDamage((Player) source, (Player) target)) {
                event.setCancelled(true);
                EventUtil.sendNoPermissionMessage((Player) source);
            }
            return;
        }

        if (!ps.hasPermission((Player) source, plotOpt.get(), Flags.DAMAGE)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) source);
        }
    }

    @Listener(beforeModifications = true)
    public void onDamage(AttackEntityTownEvent event) {
        final Entity target = event.getTargetEntity();

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(target.getLocation());

        if (!plotOpt.isPresent()) {
            return;
        }

        if (target instanceof Hostile) {
            return;
        }

        final Object source = EventUtil.getDamageSource(event);

        if (!(source instanceof Player)) {
            return;
        }

        if (target instanceof Player) {
            final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
            final Town t = ts.get(plotOpt.get().getTown()).get();
            if (!t.getPvP().canDamage((Player) source, (Player) target)) {
                event.setCancelled(true);
                EventUtil.sendNoPermissionMessage((Player) source);
            }
            return;
        }

        if (!ps.hasPermission((Player) source, plotOpt.get(), Flags.DAMAGE)) {
            event.setCancelled(true);
            EventUtil.sendNoPermissionMessage((Player) source);
        }
    }
}
