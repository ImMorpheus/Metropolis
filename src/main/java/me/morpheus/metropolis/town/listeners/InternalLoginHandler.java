package me.morpheus.metropolis.town.listeners;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.town.SimpleTownService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public final class InternalLoginHandler {

    private final SimpleTownService ts;

    public InternalLoginHandler(SimpleTownService ts) {
        this.ts = ts;
    }

    @Listener(beforeModifications = true)
    public void onLogin(ClientConnectionEvent.Join event) {
        final Player p = event.getTargetEntity();
        final Optional<CitizenData> cdOpt = p.get(CitizenData.class);

        if (!cdOpt.isPresent()) {
            return;
        }

        if (!this.ts.exist(cdOpt.get().town().get().intValue())) {
            final Text disband = TextUtil.watermark(TextColors.RED, "Your town was disbanded");
            p.sendMessage(disband);
            p.remove(CitizenData.class);
        }
    }

    @Listener(beforeModifications = true)
    public void onAuth(ClientConnectionEvent.Auth event) {
        if (this.ts.isReady()) {
            return;
        }
        event.setMessage(Text.of("Server is still loading the towns! Please wait before reconnecting."));
        event.setCancelled(true);
    }

}
