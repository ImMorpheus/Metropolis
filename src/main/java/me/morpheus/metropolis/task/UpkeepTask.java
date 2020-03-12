package me.morpheus.metropolis.task;

import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.EconomyUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public final class UpkeepTask implements Consumer<Task> {

    public static final int MAX_UPKEEP_PER_TICK = 100;

    private final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
    private final Iterator<Town> towns;

    public UpkeepTask() {
        this.towns = Sponge.getServiceManager().provideUnchecked(TownService.class).towns()
                .iterator();
    }

    @Override
    public void accept(Task task) {
        for (int i = 0; i < UpkeepTask.MAX_UPKEEP_PER_TICK && this.towns.hasNext(); i++) {
            final Town town = this.towns.next();
            final Optional<Account> bankOpt = town.getBank();
            if (bankOpt.isPresent()) {
                final BigDecimal upkeep = town.getUpkeep();
                ResultType result = EconomyUtil.withdraw(town.getBank().get(), this.es.getDefaultCurrency(), upkeep);
                if (result == ResultType.ACCOUNT_NO_FUNDS) {
                    town.disband();
                }
            }
        }
        if (!this.towns.hasNext()) {
            task.cancel();
        }
    }
}
