package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.block.ChangeBlockTownEvent;
import me.morpheus.metropolis.api.event.entity.AttackEntityTownEvent;
import me.morpheus.metropolis.api.event.entity.DamageEntityTownEvent;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.event.block.MPChangeBlockTownEventPre;
import me.morpheus.metropolis.event.entity.MPAttackEntityTownEvent;
import me.morpheus.metropolis.event.entity.MPDamageEntityTownEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

public final class InternalDamageEntityHandler {

    private final PlotService ps;

    public InternalDamageEntityHandler(PlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onBlockPre(DamageEntityEvent event) {
        final Entity target = event.getTargetEntity();
        final Optional<PlotData> pdOpt = this.ps.get(target.getLocation());

        if (!pdOpt.isPresent()) {
            return;
        }
        DamageEntityTownEvent townEvent = new MPDamageEntityTownEvent(event.getCause(), event.getTargetEntity());
        if (Sponge.getEventManager().post(townEvent)) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onBlockPre(AttackEntityEvent event) {
        final Entity target = event.getTargetEntity();
        final Optional<PlotData> pdOpt = this.ps.get(target.getLocation());

        if (!pdOpt.isPresent()) {
            return;
        }
        AttackEntityTownEvent townEvent = new MPAttackEntityTownEvent(event.getCause(), event.getTargetEntity());
        if (Sponge.getEventManager().post(townEvent)) {
            event.setCancelled(true);
        }
    }
}
