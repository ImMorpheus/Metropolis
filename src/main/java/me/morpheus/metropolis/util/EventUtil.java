package me.morpheus.metropolis.util;

import me.morpheus.metropolis.api.event.entity.AttackEntityTownEvent;
import me.morpheus.metropolis.api.event.entity.DamageEntityTownEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Optional;

public final class EventUtil {

    @Nullable
    public static User getSource(Event event) {
        final Optional<User> ownerOpt = event.getContext().get(EventContextKeys.OWNER);
        if (ownerOpt.isPresent()) {
            return ownerOpt.get();
        }

        final Optional<User> notifierOpt = event.getContext().get(EventContextKeys.NOTIFIER);
        if (notifierOpt.isPresent()) {
            return notifierOpt.get();
        }

        return null;
    }

    public static Object getDamageSource(DamageEntityTownEvent event) {
        final Object root = event.getCause().root();

        if (root instanceof IndirectEntityDamageSource) {
            return ((IndirectEntityDamageSource) root).getIndirectSource();
        }

        if (root instanceof EntityDamageSource) {
            return ((EntityDamageSource) root).getSource();
        }

        return root;
    }

    public static Object getDamageSource(AttackEntityTownEvent event) {
        final Object root = event.getCause().root();

        if (root instanceof IndirectEntityDamageSource) {
            return ((IndirectEntityDamageSource) root).getIndirectSource();
        }

        if (root instanceof EntityDamageSource) {
            return ((EntityDamageSource) root).getSource();
        }

        return root;
    }

    public static void sendNoPermissionMessage(Player player) {
        player.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have permission to do this"));
    }

    private EventUtil() {}
}
