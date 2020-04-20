package me.morpheus.metropolis.plot.listeners;

import me.morpheus.metropolis.plot.SimplePlotService;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

public final class InternalLoginHandler {

    private final SimplePlotService ps;

    public InternalLoginHandler(SimplePlotService ps) {
        this.ps = ps;
    }

    @Listener(beforeModifications = true)
    public void onAuth(ClientConnectionEvent.Auth event) {
        if (this.ps.isReady()) {
            return;
        }
        event.setMessage(Text.of("Server is still loading the plots! Please wait before reconnecting."));
        event.setCancelled(true);
    }

}
