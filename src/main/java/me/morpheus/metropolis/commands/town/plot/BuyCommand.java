package me.morpheus.metropolis.commands.town.plot;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.EconomyUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
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

class BuyCommand extends AbstractHomeTownCommand {

    BuyCommand() {
        super(
                PlotDispatcher.PERM + ".buy.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        if (!plot.isForSale()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This plot is not for sale"));
            return CommandResult.empty();
        }
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

        final BigDecimal amount = BigDecimal.valueOf(plot.getPrice());
        final Currency currency = es.getDefaultCurrency();
        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
            frame.addContext(EventContextKeys.PLUGIN, plugin);
            final ResultType result = accOpt.get().transfer(bOpt.get(), currency, amount, frame.getCurrentCause()).getResult();
            if (result != ResultType.SUCCESS) {
                final String error = EconomyUtil.getErrorMessage(result);
                source.sendMessage(TextUtil.watermark(TextColors.RED, error));
                return CommandResult.empty();
            }
        }
        plot.setOwner(source.getUniqueId());
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "You bought this plot for ", currency.format(amount)));
        plot.setPrice(0.0);
        plot.setForSale(false);

        return CommandResult.success();
    }
}
