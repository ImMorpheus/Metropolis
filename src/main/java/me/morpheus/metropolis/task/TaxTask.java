package me.morpheus.metropolis.task;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.economy.TaxData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.EconomyUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.user.UserStorageService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public final class TaxTask implements Consumer<Task> {

    public static final int MAX_TAX_PER_TICK = 100;

    private final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
    private final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
    private final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
    private final Iterator<GameProfile> profiles;

    public TaxTask(Collection<GameProfile> profiles) {
        this.profiles = profiles.iterator();
    }

    @Override
    public void accept(Task task) {
        for (int i = 0; i < TaxTask.MAX_TAX_PER_TICK && this.profiles.hasNext(); i++) {
            final GameProfile profile = this.profiles.next();
            final Optional<User> userOpt = this.uss.get(profile);
            if (userOpt.isPresent()) {
                final User user = userOpt.get();
                final Optional<CitizenData> cdOpt = user.get(CitizenData.class);
                if (cdOpt.isPresent()) {
                    final CitizenData cd = cdOpt.get();
                    if (!cd.rank().get().isTaxExempt()) {
                        final Optional<Town> townOpt = this.ts.get(cd.town().get().intValue());
                        if (townOpt.isPresent()) {
                            final Town town = townOpt.get();
                            final Optional<TaxData> taxOpt = town.get(TaxData.class);
                            if (taxOpt.isPresent()) {
                                final Optional<UniqueAccount> fromOpt = this.es.getOrCreateAccount(user.getUniqueId());
                                final Optional<Account> toOpt = town.getBank();
                                if (fromOpt.isPresent() && toOpt.isPresent()) {
                                    ResultType result = EconomyUtil.transfer(fromOpt.get(), toOpt.get(), this.es.getDefaultCurrency(), BigDecimal.valueOf(taxOpt.get().tax().get()));
                                    if (result == ResultType.ACCOUNT_NO_FUNDS) {
                                        town.kick(user.getUniqueId());
                                    }
                                }
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
