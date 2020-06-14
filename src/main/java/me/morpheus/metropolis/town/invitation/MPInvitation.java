package me.morpheus.metropolis.town.invitation;

import com.google.common.base.MoreObjects;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.rank.Ranks;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.town.invitation.Invitation;
import me.morpheus.metropolis.api.town.invitation.InvitationService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class MPInvitation implements Invitation {

    @Nullable private final UUID source;
    private final UUID target;
    private final int town;
    private final Instant instant;

    public MPInvitation(@Nullable UUID source, UUID target, int town, Instant instant) {
        this.source = source;
        this.target = target;
        this.town = town;
        this.instant = instant;
    }

    @Override
    public Optional<UUID> getSource() {
        return Optional.ofNullable(this.source);
    }

    @Override
    public UUID getTarget() {
        return this.target;
    }

    @Override
    public Optional<Town> getTown() {
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        return ts.get(this.town);
    }

    @Override
    public Instant getInstant() {
        return this.instant;
    }

    @Override
    public boolean isValid() {
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        if (!ts.exist(this.town)) {
            return false;
        }
        final GlobalConfig global = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();

        if (Instant.now().isAfter(this.instant.plus(global.getTownCategory().getInvitationDuration()))) {
            return false;
        }
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        if (this.source != null) {
            Optional<User> sourceOpt = uss.get(this.source);
            if (!sourceOpt.isPresent()) {
                return false;
            }
            Optional<CitizenData> cdOpt = sourceOpt.get().get(CitizenData.class);
            if (!cdOpt.isPresent()) {
                return false;
            }
            if (cdOpt.get().town().get().intValue() != this.town) {
                return false;
            }
            final Set<Context> contexts = new HashSet<>(sourceOpt.get().getActiveContexts());
            contexts.add(new Context("townrank", cdOpt.get().rank().get().getId()));
            if (!sourceOpt.get().hasPermission(contexts, Metropolis.ID + ".commands.town.invite.base")) {
                return false;
            }
        }

        Optional<User> targetOpt = uss.get(this.target);
        return targetOpt.isPresent() && !targetOpt.get().get(CitizenData.class).isPresent();
    }

    @Override
    public boolean accept() {
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        Optional<User> targetOpt = uss.get(this.target);
        if (!isValid()) {
            targetOpt.flatMap(User::getPlayer).ifPresent(p -> p.sendMessage(TextUtil.watermark(TextColors.RED, "This invitation is no longer valid")));
            remove();
            return false;
        }
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        ts.get(this.town).get().accept(this.target, Ranks.CITIZEN);
        return true;
    }

    @Override
    public boolean refuse() {
        remove();
        return true;
    }

    private void remove() {
        Sponge.getServiceManager().provideUnchecked(InvitationService.class).remove(this.target, invitation -> invitation == this);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Invitation && Objects.equals(this.source, ((Invitation) obj).getSource().orElse(null))
                && this.target.equals(((Invitation) obj).getTarget()) && this.instant.equals(((Invitation) obj).getInstant())
                && getTown().equals(((Invitation) obj).getTown()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.target, this.source, this.instant) + (this.town * 13);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Source", this.source)
                .add("Target", this.target)
                .add("Instant", this.instant)
                .add("Town", this.town)
                .toString();
    }
}
