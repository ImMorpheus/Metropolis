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
import java.util.EnumMap;
import java.util.Optional;

public final class EconomyUtil {

    private static final EnumMap<ResultType, String> ERRORS = new EnumMap<>(ResultType.class);
    static {
        ERRORS.put(ResultType.ACCOUNT_NO_FUNDS, "Not enough money");
    }

    public static String getErrorMessage(ResultType result) {
        if (result == ResultType.SUCCESS) {
            throw new IllegalArgumentException("SUCCESS is not an error");
        }
        final String error = ERRORS.get(result);
        if (error == null) {
            return "Error while paying: " + result.name();
        }
        return error;
    }

    private EconomyUtil() {}
}
