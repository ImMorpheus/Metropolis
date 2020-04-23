package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;

public final class MoveEntityTownHandler {

    @Listener(beforeModifications = true)
    public void onMove(MoveEntityPlotEvent event) {
        if (event.getTargetEntity().getType() != EntityTypes.PLAYER) {
            return;
        }
        if (event.getMessage().isEmpty()) {
            return;
        }

        final Player player = (Player) event.getTargetEntity();

        player.sendMessage(TextUtil.watermark(event.getMessage()));
    }
}
