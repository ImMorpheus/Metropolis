package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.flag.Flags;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.util.EventUtil;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.world.LocatableBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public final class InternalNotifyHandler {

    private final PlotService ps;

    public InternalNotifyHandler(PlotService ps) {
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

        event.getNeighbors().entrySet().removeIf(entry -> {

            if (entry.getValue().getType() == BlockTypes.AIR) {
                return false;
            }

            final Location<World> loc = locatable.getLocation().add(entry.getKey().asOffset());
            final Optional<PlotData> pdOpt = this.ps.get(loc);

            return pdOpt.isPresent() && !EventUtil.hasPermission(source, pdOpt.get(), Flags.BLOCK_CHANGE);
        });

    }
}
