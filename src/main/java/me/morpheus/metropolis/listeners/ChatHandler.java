package me.morpheus.metropolis.listeners;

import com.google.common.collect.ImmutableMap;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class ChatHandler {

    @Listener(order = Order.LATE, beforeModifications = true)
    public void onChat(MessageChannelEvent.Chat event, @Root Player player) {
        final Optional<CitizenData> cdOpt = player.get(CitizenData.class);

        if (!cdOpt.isPresent()) {
            return;
        }

        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        final Optional<Town> tOpt = ts.get(cdOpt.get().town().get().intValue());

        if (!tOpt.isPresent()) {
            return;
        }

        if (tOpt.get().getMessageChannel() == event.getChannel().orElse(null)) {
            return;
        }

        final GlobalConfig g = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        Text header = g.getChatCategory().getPrefix()
                .apply(ImmutableMap.of("tag", tOpt.get().getTag(), "rank", cdOpt.get().rank().get().getName()))
                .build();

        event.getFormatter().setHeader(header.concat(event.getFormatter().getHeader().toText()));
    }
}
