package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.town.visibility.Visibilities;
import me.morpheus.metropolis.util.EconomyUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

class SpawnCommand extends AbstractPlayerCommand {

    SpawnCommand() {
        super(MPGenericArguments.townOrHomeTown(Text.of("town")), InputTokenizer.rawInput());
    }

    @Override
    public CommandResult process(Player source, CommandContext context) throws CommandException {
        final Optional<Town> townOpt = context.getOne("town");

        if (!townOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        final Optional<CitizenData> cdOpt = source.get(CitizenData.class);

        if (townOpt.get().getVisibility() != Visibilities.PUBLIC && (!cdOpt.isPresent() || cdOpt.get().town().get().intValue() != townOpt.get().getId())) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This town is not public"));
            return CommandResult.empty();
        }

        final GlobalConfig global = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        if (global.getEconomyCategory().isEnabled()) {
            final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
            final Optional<UniqueAccount> accOpt = es.getOrCreateAccount(source.getUniqueId());
            if (!accOpt.isPresent()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to retrieve player account"));
                return CommandResult.empty();
            }
            final ResultType result = EconomyUtil.withdraw(accOpt.get(), es.getDefaultCurrency(), BigDecimal.valueOf(townOpt.get().getType().getSpawnPrice()));
            if (result == ResultType.ACCOUNT_NO_FUNDS) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Not enough money"));
                return CommandResult.empty();
            }
            if (result != ResultType.SUCCESS) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Error while paying: ", result.name()));
                return CommandResult.empty();
            }
        }
        source.transferToWorld(townOpt.get().getSpawn().getExtent(), townOpt.get().getSpawn().getPosition());
        return CommandResult.success();
    }

    @Override
    public boolean testPermission(Player player) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("t spawn."));
    }
}
