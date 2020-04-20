package me.morpheus.metropolis.plot.listeners;

import com.flowpowered.math.vector.Vector2i;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flags;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.util.EventUtil;
import me.morpheus.metropolis.util.VectorUtil;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public final class InternalNotifyHandler {

    private final SimplePlotService ps;

    public InternalNotifyHandler(SimplePlotService ps) {
        this.ps = ps;
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onNotify(NotifyNeighborBlockEvent event) {
        final LocatableBlock locatable = event.getCause().first(LocatableBlock.class).get();
        final User source = event.getCause().first(User.class)
                .orElse(event.getCause().getContext().get(EventContextKeys.NOTIFIER)
                        .orElse(event.getCause().getContext().get(EventContextKeys.OWNER)
                                .orElse(null)));

        if (source == null) {
            return;
        }

        final Map<Vector2i, Plot> wm = this.ps.get(locatable.getLocation().getExtent().getUniqueId());
        if (wm == null) {
            return;
        }
        CitizenData cd = null;
        boolean outsider = false;
        final Reference2ByteMap<Plot> cache = new Reference2ByteOpenHashMap<>();
        cache.defaultReturnValue(Byte.MIN_VALUE);
        final byte accepted = 0b0;
        final byte refused = 0b1;
        final Iterator<Map.Entry<Direction, BlockState>> each = event.getNeighbors().entrySet().iterator();
        while (each.hasNext()) {
            final Map.Entry<Direction, BlockState> entry = each.next();
            if (entry.getValue().getType() == BlockTypes.AIR) {
                continue;
            }
            if (outsider) {
                each.remove();
                continue;
            }

            final Location<World> loc = locatable.getLocation().add(entry.getKey().asOffset());
            final Vector2i chunk = VectorUtil.toChunk2i(loc);
            final Plot plot = wm.get(chunk);

            if (plot == null) {
                continue;
            }

            final byte cached = cache.getByte(plot);
            if (cached == accepted) {
                continue;
            }
            if (cached == refused) {
                each.remove();
                continue;
            }

            if (cd == null) {
                final Optional<CitizenData> cdOpt = source.get(CitizenData.class);
                if (cdOpt.isPresent()) {
                    cd = cdOpt.get();
                } else {
                    outsider = true;
                    each.remove();
                    continue;
                }
            }
            final boolean hasPermission = this.ps.hasPermission(source, cd, plot, Flags.BLOCK_CHANGE);
            if (hasPermission) {
                cache.put(plot, accepted);
            } else {
                cache.put(plot, refused);
                each.remove();
            }
        }

    }
}
