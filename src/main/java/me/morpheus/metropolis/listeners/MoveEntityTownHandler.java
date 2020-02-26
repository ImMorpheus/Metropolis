package me.morpheus.metropolis.listeners;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.entity.MoveEntityPlotEvent;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public final class MoveEntityTownHandler {

    @Listener(beforeModifications = true)
    public void onMove(MoveEntityPlotEvent event) {
        if (event.getTargetEntity().getType() != EntityTypes.PLAYER) {
            return;
        }

        final Player player = (Player) event.getTargetEntity();
        final Optional<PlotData> toOpt = event.getToPlot();

        if (!toOpt.isPresent()) {
            player.sendMessage(TextUtil.watermark(TextColors.GREEN, "~ Wilderness"));
            return;
        }

        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        final Town t = ts.get(toOpt.get().town().get().intValue()).get();

        final Text pvp = Text.of(TextColors.RED, "[PvP ", t.getPvP().getName(), "]");
        player.sendMessage(TextUtil.watermark(TextColors.AQUA, "~ ", t.getName(), TextColors.GRAY, " - ", pvp));
    }
}
