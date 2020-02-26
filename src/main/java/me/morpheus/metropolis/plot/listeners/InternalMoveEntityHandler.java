package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import me.morpheus.metropolis.event.entity.MPMoveEntityPlotEvent;
import me.morpheus.metropolis.plot.SimplePlotService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.World;

import java.util.Optional;

public final class InternalMoveEntityHandler {

    private final SimplePlotService ps;

    public InternalMoveEntityHandler(SimplePlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onMove(MoveEntityEvent.Position event) {
        final Entity entity = event.getTargetEntity();
        final Transform<World> from = event.getFromTransform();
        final Transform<World> to = event.getToTransform();

        if (from.getLocation().getChunkPosition().equals(to.getLocation().getChunkPosition())) {
            return;
        }

        final Optional<PlotData> toPdOpt = this.ps.get(to.getLocation());
        final Optional<PlotData> fromPdOpt = this.ps.get(from.getLocation());
        if (!toPdOpt.isPresent() && !fromPdOpt.isPresent()) {
            return;
        }

        MoveEntityPlotEvent townEvent = new MPMoveEntityPlotEvent(event.getCause(), entity, fromPdOpt.orElse(null), toPdOpt.orElse(null));
        if (Sponge.getEventManager().post(townEvent)) {
            event.setCancelled(true);
        }
    }
}
