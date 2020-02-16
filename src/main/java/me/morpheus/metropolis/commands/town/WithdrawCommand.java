package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.EconomyUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class WithdrawCommand extends AbstractCitizenCommand {

    WithdrawCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.bigDecimal(Text.of("amount"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.withdraw",
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

        final BigDecimal amount = context.requireOne("amount");
        final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);

        final Optional<Account> accOpt = es.getOrCreateAccount(source.getIdentifier());
        if (!accOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to retrieve player account"));
            return CommandResult.empty();
        }
        final ResultType result = EconomyUtil.transfer(bOpt.get(), accOpt.get(), es.getDefaultCurrency(), amount);
        if (result == ResultType.ACCOUNT_NO_FUNDS) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Not enough money"));
            return CommandResult.empty();
        }
        if (result != ResultType.SUCCESS) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Error while paying: ", result.name()));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
