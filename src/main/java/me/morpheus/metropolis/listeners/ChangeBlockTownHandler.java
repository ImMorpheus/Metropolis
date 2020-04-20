package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.event.block.ChangeBlockTownEvent;
import me.morpheus.metropolis.api.flag.Flags;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.util.EventUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public final class ChangeBlockTownHandler {

    @Listener(beforeModifications = true)
    public void onBlockPre(ChangeBlockTownEvent.Pre event) {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Object root = event.getCause().root();
        final User source = EventUtil.getSource(event);
        final Object cause = source == null ? root : source;
        final boolean isPlayer = cause instanceof Player;

        if (!isPlayer) {
            if (event.getLocations().anyMatch(location -> ps.get(location).isPresent())) {
                event.setCancelled(true);
            }
            return;
        }

        if (event.getContext().containsKey(EventContextKeys.PLAYER_BREAK)) {
            boolean notAllowed = event.getLocations().anyMatch(location ->
                    ps.get(location)
                            .filter(plotData -> !ps.hasPermission((Player) cause, plotData, Flags.BLOCK_BREAK))
                            .isPresent());
            if (notAllowed) {
                event.setCancelled(true);
            }
        } else {
            boolean notAllowed = event.getLocations().anyMatch(location ->
                    ps.get(location)
                            .filter(plotData -> !ps.hasPermission((Player) cause, plotData, Flags.BLOCK_CHANGE))
                            .isPresent()
            );
            if (notAllowed) {
                event.setCancelled(true);
            }
        }
    }

    @Listener(beforeModifications = true)
    public void onBlockPlace(ChangeBlockTownEvent.Place event) {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Object root = event.getCause().root();
        final User source = EventUtil.getSource(event);
        final Object cause = source == null ? root : source;
        final boolean isPlayer = cause instanceof Player;

        if (!isPlayer) {
            if (event.getTransactions().anyMatch(transaction ->
                    ps.get(transaction.getOriginal().getLocation().get()).isPresent())) {
                event.setCancelled(true);
            }
            return;
        }

        boolean notAllowed = event.getTransactions().anyMatch(transaction ->
                ps.get(transaction.getOriginal().getLocation().get())
                        .filter(plotData -> !ps.hasPermission((Player) cause, plotData, Flags.BLOCK_PLACE))
                        .isPresent()
        );
        if (notAllowed) {
            event.setCancelled(true);
            if (source != null) {
                source.getPlayer().ifPresent(EventUtil::sendNoPermissionMessage);
            }
        }
    }

    @Listener(beforeModifications = true)
    public void onBlockBreak(ChangeBlockTownEvent.Break event) {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Object root = event.getCause().root();
        final User source = EventUtil.getSource(event);
        final Object cause = source == null ? root : source;
        final boolean isPlayer = cause instanceof Player;

        if (!isPlayer) {
            if (event.getTransactions().anyMatch(transaction ->
                    ps.get(transaction.getOriginal().getLocation().get()).isPresent())) {
                event.setCancelled(true);
            }
            return;
        }

        boolean notAllowed = event.getTransactions().anyMatch(transaction ->
                ps.get(transaction.getOriginal().getLocation().get())
                        .filter(plotData -> !ps.hasPermission((Player) cause, plotData, Flags.BLOCK_BREAK))
                        .isPresent()
        );
        if (notAllowed) {
            event.setCancelled(true);
            if (source != null) {
                source.getPlayer().ifPresent(EventUtil::sendNoPermissionMessage);
            }
        }
    }
}
