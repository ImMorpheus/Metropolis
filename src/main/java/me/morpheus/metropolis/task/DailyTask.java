package me.morpheus.metropolis.task;

import me.morpheus.metropolis.Metropolis;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.Collection;

public final class DailyTask {

    public static void run() {
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        final Collection<GameProfile> users = uss.getAll();

        //TODO no


        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+inactive-kick")
                .intervalTicks(1L)
                .execute(new InactiveCitizenTask(users))
                .submit(plugin);

        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+tax")
                .delayTicks(6000)
                .intervalTicks(1L)
                .execute(new TaxTask(users))
                .submit(plugin);

        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+rent")
                .delayTicks(12000)
                .intervalTicks(1L)
                .execute(new RentTask())
                .submit(plugin);

        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+upkeep")
                .delayTicks(18000)
                .intervalTicks(1L)
                .execute(new UpkeepTask())
                .submit(plugin);
    }
}
