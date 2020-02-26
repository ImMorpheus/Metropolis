package me.morpheus.metropolis.town.listeners;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class InternalLoginHandler {

    @Listener(beforeModifications = true)
    public void onLogin(ClientConnectionEvent.Join event) {
        final Player p = event.getTargetEntity();
        final Optional<CitizenData> cdOpt = p.get(CitizenData.class);

        if (!cdOpt.isPresent()) {
            return;
        }

        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);

        if (!ts.exist(cdOpt.get().town().get().intValue())) {
            final Text disband = TextUtil.watermark(TextColors.RED, "Your town was disbanded");
            p.sendMessage(disband);
            p.remove(CitizenData.class);
        }
    }


}
