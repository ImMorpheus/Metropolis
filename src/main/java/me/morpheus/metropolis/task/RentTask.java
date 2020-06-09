package me.morpheus.metropolis.task;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.EconomyUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.user.UserStorageService;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public final class RentTask implements Consumer<Task> {

    public static final int MAX_RENT_PER_TICK = 100;

    private final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
    private final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
    private final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
    private final Iterator<Plot> plots;

    public RentTask() {
        this.plots = Sponge.getServiceManager().provideUnchecked(PlotService.class).plots()
                .filter(plot -> plot.getRent() != 0.0)
                .filter(plot -> plot.getOwner().isPresent())
                .iterator();
    }

    @Override
    public void accept(Task task) {
        final Currency currency = this.es.getDefaultCurrency();
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        for (int i = 0; i < RentTask.MAX_RENT_PER_TICK && this.plots.hasNext(); i++) {
            final Plot plot = this.plots.next();
            final Optional<Town> townOpt = this.ts.get(plot.getTown());
            if (townOpt.isPresent()) {
                final Optional<User> uOpt = this.uss.get(plot.getOwner().get());
                if (uOpt.isPresent()) {
                    final User user = uOpt.get();
                    Optional<CitizenData> cdOpt = user.get(CitizenData.class);
                    if (cdOpt.isPresent()) {
                        final Town town = townOpt.get();
                        final CitizenData cd = cdOpt.get();
                        if (cd.town().get().intValue() == town.getId()) {
                            final Optional<UniqueAccount> fromOpt = this.es.getOrCreateAccount(user.getUniqueId());
                            final Optional<Account> toOpt = town.getBank();
                            if (fromOpt.isPresent() && toOpt.isPresent()) {
                                final BigDecimal amount = BigDecimal.valueOf(plot.getRent());
                                try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                                    frame.addContext(EventContextKeys.PLUGIN, plugin);
                                    ResultType result = fromOpt.get().transfer(toOpt.get(), currency, amount, frame.getCurrentCause()).getResult();
                                    if (result == ResultType.ACCOUNT_NO_FUNDS) {
                                        plot.setOwner(null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!this.plots.hasNext()) {
            task.cancel();
        }
    }
}
