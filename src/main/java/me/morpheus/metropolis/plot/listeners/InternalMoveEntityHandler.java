package me.morpheus.metropolis.plot.listeners;

import com.flowpowered.math.vector.Vector2i;
import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.event.entity.MPMoveEntityPlotEvent;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.util.VectorUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.world.World;

import java.util.Map;
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

        final Map<Vector2i, Plot> wm = this.ps.get(entity.getWorld().getUniqueId());
        if (wm == null) {
            return;
        }

        final Plot toPd = wm.get(VectorUtil.toChunk2i(to.getLocation()));
        final Plot fromPd = wm.get(VectorUtil.toChunk2i(from.getLocation()));
        if (toPd == null && fromPd == null) {
            return;
        }

        final MoveEntityPlotEvent townEvent = new MPMoveEntityPlotEvent(event.getCause(), entity, fromPd, toPd);
        if (Sponge.getEventManager().post(townEvent)) {
            event.setCancelled(true);
        }
    }
}
