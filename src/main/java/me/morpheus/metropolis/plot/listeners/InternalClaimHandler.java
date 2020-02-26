package me.morpheus.metropolis.plot.listeners;

import com.flowpowered.math.vector.Vector2i;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.plot.ClaimPlotEvent;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.util.VectorUtil;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;

import javax.annotation.Nullable;
import java.util.UUID;

public final class InternalClaimHandler {

    private final SimplePlotService ps;

    public InternalClaimHandler(SimplePlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onClaimPre(ClaimPlotEvent.Pre event) {
        final PlotType type = event.getPlot().type().get();
        if (type == PlotTypes.HOMEBLOCK) {
            return;
        }

        final UUID world = event.getLocation().getExtent().getUniqueId();
        final Vector2i cp = VectorUtil.toChunk2i(event.getLocation());

        if (type == PlotTypes.OUTPOST) {
            final PlotData e = this.ps.get(world, cp.add(1, 0));
            final PlotData w = this.ps.get(world, cp.add(-1, 0));
            final PlotData s = this.ps.get(world, cp.add(0, 1));
            final PlotData n = this.ps.get(world, cp.add(0, -1));
            if (e != null || w != null || s != null || n != null) {
                event.setCancelled(true);
            }
            return;
        }

        final int town = event.getPlot().town().get();
        final PlotData e = this.ps.get(world, cp.add(1, 0));
        final PlotData w = this.ps.get(world, cp.add(-1, 0));
        final PlotData s = this.ps.get(world, cp.add(0, 1));
        final PlotData n = this.ps.get(world, cp.add(0, -1));
        if (isNotTownPlot(town, e) && isNotTownPlot(town, w) && isNotTownPlot(town, s) && isNotTownPlot(town, n)) {
            event.setCancelled(true);
        }
    }

    private boolean isNotTownPlot(int id, @Nullable PlotData pd) {
        return pd == null || pd.town().get() != id;
    }
}
