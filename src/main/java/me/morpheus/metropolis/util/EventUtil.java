package me.morpheus.metropolis.util;

import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.event.entity.AttackEntityTownEvent;
import me.morpheus.metropolis.api.event.entity.DamageEntityTownEvent;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.rank.Rank;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

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

    public static boolean hasPermission(User source, PlotData pd, Flag flag) {
        final Optional<CitizenData> cdOpt = source.get(CitizenData.class);
        return cdOpt.isPresent() && hasPermission(source.getUniqueId(), cdOpt.get(), pd, flag);
    }

    public static boolean hasPermission(UUID source, CitizenData cd, PlotData pd, Flag flag) {
        if (pd.town().get().intValue() != cd.town().get().intValue()) {
            return false;
        }

        final Rank rank = cd.rank().get();

        final byte value = pd.getPermission(flag);
        if (value != Byte.MIN_VALUE) {
            return rank.getPermission(flag) >= value;
        }

        final GlobalConfig config = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        final byte perm = rank.getPermission(flag);

        if (!pd.owner().get().isPresent()) {
            return perm > config.getTownCategory().getPlotCategory().getUnownedPermission(flag);
        }

        final UUID ownerId = pd.owner().get().get();
        if (ownerId.equals(source)) {
            return true;
        }

        final User owner = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(ownerId).get();

        final Optional<CitizenData> optOwnerData = owner.get(CitizenData.class);
        if (!optOwnerData.isPresent()) {
            return perm > config.getTownCategory().getPlotCategory().getUnownedPermission(flag);
        }

        if (optOwnerData.get().friends().contains(source)) {
            return true;
        }

        return perm > optOwnerData.get().rank().get().getPermission(flag);
    }

    public static void sendNoPermissionMessage(Player player) {
        player.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have permission to do this"));
    }

    private EventUtil() {}
}
