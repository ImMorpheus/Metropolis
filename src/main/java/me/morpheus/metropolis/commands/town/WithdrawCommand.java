package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.EconomyUtil;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

class WithdrawCommand extends AbstractCitizenCommand {

    WithdrawCommand() {
        super(
                MPGenericArguments.positiveBigDecimal(Text.of("amount")),
                MinimalInputTokenizer.INSTANCE,
                TownDispatcher.PERM + ".withdraw.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Optional<Account> bOpt = t.getBank();
        if (!bOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to retrieve Town bank"));
            return CommandResult.empty();
        }

        final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);

        final Optional<UniqueAccount> accOpt = es.getOrCreateAccount(source.getUniqueId());
        if (!accOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to retrieve player account"));
            return CommandResult.empty();
        }

        final BigDecimal amount = context.requireOne("amount");
        final Currency currency = es.getDefaultCurrency();

        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
            frame.addContext(EventContextKeys.PLUGIN, plugin);
            final ResultType result = bOpt.get().transfer(accOpt.get(), currency, amount, frame.getCurrentCause()).getResult();
            if (result != ResultType.SUCCESS) {
                final String error = EconomyUtil.getErrorMessage(result);
                source.sendMessage(TextUtil.watermark(TextColors.RED, error));
                return CommandResult.empty();
            }
        }

        final Text sourceName = NameUtil.getDisplayName(source);
        t.sendMessage(Text.of(sourceName, " withdrew ", currency.format(amount), " from the town bank"));
        return CommandResult.success();
    }
}
