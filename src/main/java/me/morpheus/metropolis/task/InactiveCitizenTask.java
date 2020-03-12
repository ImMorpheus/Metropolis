package me.morpheus.metropolis.task;

import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public final class InactiveCitizenTask implements Consumer<Task> {

    public static final int MAX_INACTIVE_USER_KICK_PER_TICK = 100;

    private final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
    private final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
    private final Iterator<GameProfile> profiles;
    private final Duration inactivity;

    public InactiveCitizenTask(Collection<GameProfile> profiles) {
        this.profiles = profiles.iterator();
        this.inactivity = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal().getTownCategory().getKickForInactivity();
    }

    @Override
    public void accept(Task task) {
        for (int i = 0; i < InactiveCitizenTask.MAX_INACTIVE_USER_KICK_PER_TICK && this.profiles.hasNext(); i++) {
            final GameProfile profile = this.profiles.next();
            final Optional<User> userOpt = this.uss.get(profile);
            if (userOpt.isPresent()) {
                final User user = userOpt.get();
                final Optional<CitizenData> cdOpt = user.get(CitizenData.class);
                if (cdOpt.isPresent()) {
                    final CitizenData cd = cdOpt.get();
                    if (cd.rank().get().canBeKickedForInactivity()) {
                        final Instant deadline = cd.joined().get().plus(this.inactivity);
                        if (Instant.now().isAfter(deadline)) {
                            final Optional<Town> townOpt = this.ts.get(cd.town().get().intValue());
                            if (townOpt.isPresent()) {
                                townOpt.get().kick(user.getUniqueId());
                            }
                        }
                    }
                }
            }
        }
        if (!this.profiles.hasNext()) {
            task.cancel();
        }
    }
}
