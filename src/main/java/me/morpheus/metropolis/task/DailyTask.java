package me.morpheus.metropolis.task;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.plot.PlotService;
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

        final long taxDelay = (users.size() / InactiveCitizenTask.MAX_INACTIVE_USER_KICK_PER_TICK) + 20;
        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+tax")
                .delayTicks(taxDelay)
                .intervalTicks(1L)
                .execute(new TaxTask(users))
                .submit(plugin);

        final long rentDelay = taxDelay + (users.size() / TaxTask.MAX_TAX_PER_TICK) + 20;
        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+rent")
                .delayTicks(rentDelay)
                .intervalTicks(1L)
                .execute(new RentTask())
                .submit(plugin);

        final long upkeepDelay = rentDelay + (Sponge.getServiceManager().provideUnchecked(PlotService.class).plots().count() / RentTask.MAX_RENT_PER_TICK) + 20;
        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+upkeep")
                .delayTicks(upkeepDelay)
                .intervalTicks(1L)
                .execute(new UpkeepTask())
                .submit(plugin);
    }
}
