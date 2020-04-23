package me.morpheus.metropolis.plot.listeners;

import com.flowpowered.math.vector.Vector2i;
import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.event.entity.MPMoveEntityPlotEvent;
import me.morpheus.metropolis.plot.MPPlot;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.util.TextUtil;
import me.morpheus.metropolis.util.VectorUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Map;

public final class InternalMoveEntityHandler {

    private static final Text WILDERNESS = Text.of(TextColors.GREEN, "~ Wilderness");

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

        final MPPlot toPd = (MPPlot) wm.get(VectorUtil.toChunk2i(to.getLocation()));
        final MPPlot fromPd = (MPPlot) wm.get(VectorUtil.toChunk2i(from.getLocation()));
        if (toPd == null && fromPd == null) {
            return;
        }

        if (entity.getType() != EntityTypes.PLAYER) {
            if (callTownEvent(event.getCause(), entity, fromPd, toPd)) {
                event.setCancelled(true);
            }
        } else if (toPd == null) {
            final MessageEvent.MessageFormatter formatter = new MessageEvent.MessageFormatter(WILDERNESS);

            if (callTownEvent(event.getCause(), formatter, WILDERNESS, entity, fromPd, toPd)) {
                event.setCancelled(true);
            }
        } else if (fromPd != null && !needMessage(fromPd, toPd)) {
            if (callTownEvent(event.getCause(), entity, fromPd, toPd)) {
                event.setCancelled(true);
            }
        } else {
            final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
            final Town t = ts.get(toPd.getTown()).get();

            //
            final Text.Builder builder = Text.builder();
            builder.append(Text.of(TextColors.AQUA, "~ ", t.getName(), TextColors.GRAY, " - ", TextColors.RED, "[PvP ", t.getPvP().getName(), "]"));
            final String name = toPd.getOwnerName();
            if (!name.isEmpty()) {
                builder.append(Text.of(TextColors.GRAY, " - ", TextColors.GREEN, name));
            }
            if (!toPd.getName().isEmpty()) {
                builder.append(Text.of(TextColors.GRAY, " - ", TextColors.RESET, toPd.getName()));
            }
            if (toPd.isForSale()) {
                builder.append(Text.of(TextColors.GRAY, " - ", TextColors.YELLOW, "[For sale: ", toPd.getPrice(), "]"));
            }

            final Text body = builder.build();
            final MessageEvent.MessageFormatter formatter = new MessageEvent.MessageFormatter(body);

            if (callTownEvent(event.getCause(), formatter, body, entity, fromPd, toPd)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean needMessage(Plot from, Plot to) {
        if (from.getTown() != to.getTown()) {
            return true;
        }
        if (from.isForSale() != to.isForSale()) {
            return true;
        }
        if (from.getPrice() != to.getPrice()) {
            return true;
        }
        if (!from.getOwner().equals(to.getOwner())) {
            return true;
        }
        return false;
    }

    private boolean callTownEvent(Cause cause, Entity entity, @Nullable Plot from, @Nullable Plot to) {
        return callTownEvent(cause, new MessageEvent.MessageFormatter(), Text.of(), entity, from, to);
    }

    private boolean callTownEvent(Cause cause, MessageEvent.MessageFormatter formatter, Text message, Entity entity, @Nullable Plot from, @Nullable Plot to) {
        final MoveEntityPlotEvent townEvent = new MPMoveEntityPlotEvent(cause, formatter, message, entity, from, to);
        return Sponge.getEventManager().post(townEvent);

    }
}
