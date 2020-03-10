package me.morpheus.metropolis.town.economy;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.data.town.economy.TaxData;
import me.morpheus.metropolis.api.plot.PlotService;
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
import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;

public class TaxCollectionTask implements Consumer<Task> {

    private static final int MAX_USERS_PER_TICK = 100;
    private final Iterator<GameProfile> profiles;
    private Iterator<PlotData> plots;
    private Iterator<Town> towns;
    private final Int2IntMap map = new Int2IntOpenHashMap();
    {
        this.map.defaultReturnValue(0);
    }

    public TaxCollectionTask(Iterator<GameProfile> profiles) {
        this.profiles = profiles;
    }

    @Override
    public void accept(Task t) {
        final GlobalConfig global = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);

        int i = 0;
        while (i++ < TaxCollectionTask.MAX_USERS_PER_TICK && this.profiles.hasNext()) {
            final GameProfile user = this.profiles.next();
            final Optional<User> uOpt = uss.get(user);
            if (uOpt.isPresent()) {
                final Optional<CitizenData> cdOpt = uOpt.get().get(CitizenData.class);
                if (cdOpt.isPresent()) {
                    final Instant inactive = cdOpt.get().joined().get().plus(global.getTownCategory().getKickForInactivity());
                    final Optional<Town> tOpt = ts.get(cdOpt.get().town().get().intValue());
                    if (Instant.now().isAfter(inactive) && cdOpt.get().rank().get().canBeKickedForInactivity()) {
                        tOpt.ifPresent(town -> town.kick(user.getUniqueId()));
                    } else {
                        if (tOpt.isPresent()) {
                            int citizens = this.map.get(tOpt.get().getId());
                            this.map.put(tOpt.get().getId(), ++citizens);
                            if (!cdOpt.get().rank().get().isTaxExempt()) {
                                final Optional<TaxData> taxOpt = tOpt.get().get(TaxData.class);
                                if (taxOpt.isPresent()) {
                                    final Optional<UniqueAccount> fromOpt = es.getOrCreateAccount(user.getUniqueId());
                                    final Optional<Account> toOpt = tOpt.get().getBank();
                                    if (fromOpt.isPresent() && toOpt.isPresent()) {
                                        ResultType result = EconomyUtil.transfer(fromOpt.get(), toOpt.get(), es.getDefaultCurrency(), BigDecimal.valueOf(taxOpt.get().tax().get()));
                                        if (result == ResultType.ACCOUNT_NO_FUNDS) {
                                            tOpt.get().kick(user.getUniqueId());
                                        }
                                    } else {
                                        MPLog.getLogger().error("Unable to collect taxes from {} to {}", user.getName().orElse(user.getUniqueId().toString()), tOpt.get().getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!this.profiles.hasNext()) {
            if (this.plots == null) {
                this.plots = ps.plots()
                        .parallel()
                        .filter(pd -> pd.rent().get().doubleValue() != 0.0)
                        .filter(pd -> pd.owner().get().isPresent())
                        .filter(pd -> {
                            Optional<User> uOpt = uss.get(pd.owner().get().get());
                            if (uOpt.isPresent()) {
                                Optional<CitizenData> cdOpt = uOpt.get().get(CitizenData.class);
                                return cdOpt.isPresent() && cdOpt.get().town().get().intValue() == pd.town().get().intValue();
                            }
                            return false;
                        }).iterator();
            } else {
                int j = 0;
                while (j++ < TaxCollectionTask.MAX_USERS_PER_TICK && this.plots.hasNext()) {
                    final PlotData pd = this.plots.next();
                    final User user = uss.get(pd.owner().get().get()).get();
                    final Optional<Town> tOpt = ts.get(pd.town().get().intValue());
                    if (tOpt.isPresent()) {
                        final Optional<UniqueAccount> fromOpt = es.getOrCreateAccount(user.getUniqueId());
                        final Optional<Account> toOpt = tOpt.get().getBank();
                        if (fromOpt.isPresent() && toOpt.isPresent()) {
                            ResultType result = EconomyUtil.transfer(fromOpt.get(), toOpt.get(), es.getDefaultCurrency(), BigDecimal.valueOf(pd.rent().get().doubleValue()));
                            if (result == ResultType.ACCOUNT_NO_FUNDS) {
                                pd.set(PlotKeys.OWNER, Optional.empty());
                            }
                        } else {
                            MPLog.getLogger().error("Unable to create an Account for {}", user.getName());
                        }
                    }
                }
            }
            if (!this.plots.hasNext()) {
                if (this.towns == null) {
                    this.towns = ts.towns()
                            .parallel()
                            .iterator();
                } else {
                    int j = 0;
                    while (j++ < TaxCollectionTask.MAX_USERS_PER_TICK && this.towns.hasNext()) {
                        final Town town = this.towns.next();
                        final BigDecimal upkeep = town.getUpkeep();
                        if (town.getBank().isPresent()) {
                            ResultType result = EconomyUtil.withdraw(town.getBank().get(), es.getDefaultCurrency(), upkeep);
                            if (result == ResultType.ACCOUNT_NO_FUNDS) {
                                town.disband();
                            }
                        } else {
                            MPLog.getLogger().error("Unable to create an Account for {}", town.getName());
                        }
                    }
                }
                if (!this.towns.hasNext()) {
                    t.cancel();
                }
            }
        }

    }
}
