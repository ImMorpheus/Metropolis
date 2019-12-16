package me.morpheus.metropolis.util;

import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.Metropolis;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;

public final class EconomyUtil {

    public static ResultType deposit(Account account, Currency currency, BigDecimal amount) {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        final EventContext eventContext = EventContext.builder()
                .add(EventContextKeys.PLUGIN, plugin)
                .build();

        final TransactionResult result = account.deposit(currency, amount, Cause.of(eventContext, plugin));

        return result.getResult();
    }

    public static ResultType withdraw(Account account, Currency currency, BigDecimal amount) {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        final EventContext eventContext = EventContext.builder()
                .add(EventContextKeys.PLUGIN, plugin)
                .build();

        final TransactionResult result = account.withdraw(currency, amount, Cause.of(eventContext, plugin));

        return result.getResult();
    }

    public static ResultType transfer(Account from, Account to, Currency currency, BigDecimal amount) {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        final EventContext eventContext = EventContext.builder()
                .add(EventContextKeys.PLUGIN, plugin)
                .build();

        final TransactionResult result = from.transfer(to, currency, amount, Cause.of(eventContext, plugin));

        return result.getResult();
    }

    private EconomyUtil() {}
}
