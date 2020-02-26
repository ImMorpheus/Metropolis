package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.block.InteractBlockTownEvent;
import me.morpheus.metropolis.api.event.entity.InteractEntityTownEvent;
import me.morpheus.metropolis.api.event.item.inventory.InteractItemTownEvent;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.event.block.MPInteractBlockTownEventPrimary;
import me.morpheus.metropolis.event.block.MPInteractBlockTownEventSecondary;
import me.morpheus.metropolis.event.entity.MPInteractEntityTownEventPrimary;
import me.morpheus.metropolis.event.entity.MPInteractEntityTownEventSecondary;
import me.morpheus.metropolis.event.item.inventory.MPInteractItemTownEventPrimary;
import me.morpheus.metropolis.event.item.inventory.MPInteractItemTownEventSecondary;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public final class InternalInteractHandler {

    private final PlotService ps;

    public InternalInteractHandler(PlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onInteractBlock(InteractBlockEvent event) {
        if (!event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        final Optional<PlotData> pdOpt = this.ps.get(event.getTargetBlock().getLocation().get());

        if (!pdOpt.isPresent()) {
            return;
        }

        final InteractBlockTownEvent townEvent;
        if (event instanceof InteractBlockEvent.Primary) {
            townEvent = new MPInteractBlockTownEventPrimary(event.getCause(), event.getInteractionPoint().orElse(null), event.getTargetBlock());
        } else {
            townEvent = new MPInteractBlockTownEventSecondary(event.getCause(), event.getInteractionPoint().orElse(null), event.getTargetBlock());
        }
        if (Sponge.getEventManager().post(townEvent)) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onInteractItem(InteractItemEvent event) {
        if (!event.getInteractionPoint().isPresent()) {
            return;
        }

        final Object root = event.getCause().root();
        if (!(root instanceof Locatable)) {
            return;
        }

        final Optional<PlotData> pdOpt = this.ps.get(((Locatable) root).getLocation().add(event.getInteractionPoint().get()));

        if (!pdOpt.isPresent()) {
            return;
        }

        final InteractItemTownEvent townEvent;
        if (event instanceof InteractItemEvent.Primary) {
            townEvent = new MPInteractItemTownEventPrimary(event.getCause(), event.getInteractionPoint().orElse(null), event.getItemStack());
        } else {
            townEvent = new MPInteractItemTownEventSecondary(event.getCause(), event.getInteractionPoint().orElse(null), event.getItemStack());
        }
        if (Sponge.getEventManager().post(townEvent)) {
            event.setCancelled(true);
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onInteractEntity(InteractEntityEvent event) {
        final Entity entity = event.getTargetEntity();
        final Optional<PlotData> pdOpt = this.ps.get(entity.getLocation());

        if (!pdOpt.isPresent()) {
            return;
        }

        final InteractEntityTownEvent townEvent;
        if (event instanceof InteractEntityEvent.Primary) {
            townEvent = new MPInteractEntityTownEventPrimary(event.getCause(), event.getInteractionPoint().orElse(null), entity);
        } else {
            townEvent = new MPInteractEntityTownEventSecondary(event.getCause(), event.getInteractionPoint().orElse(null), entity);
        }
        if (Sponge.getEventManager().post(townEvent)) {
            event.setCancelled(true);
        }
    }


}
