package me.morpheus.metropolis.commands.town;

import com.flowpowered.math.vector.Vector3i;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
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
import org.spongepowered.api.text.serializer.TextSerializers;

import java.math.BigDecimal;
import java.util.Optional;

class ClaimCommand extends AbstractCitizenCommand {

    ClaimCommand() {
        super(
                GenericArguments.optional(
                        GenericArguments.seq(
                                MPGenericArguments.catalog(PlotType.class, Text.of("type")),
                                GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false)
                        )
                ),
                InputTokenizer.quotedStrings(false),
                Metropolis.ID + ".commands.town.claim",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final PlotType type = context.<PlotType>getOne("type").orElse(PlotTypes.PLOT);
        final Text name = context.<Text>getOne("name").orElse(null);

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(source.getLocation());

        if (plotOpt.isPresent()) {
            if (plotOpt.get().getTown() == cd.town().get().intValue()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "You've already claimed this plot"));
            } else {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "This plot is claimed by another town"));
            }
            return CommandResult.empty();
        }

        final GlobalConfig global = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        if (global.getEconomyCategory().isEnabled()) {
            final Optional<Account> accOpt = t.getBank();
            if (!accOpt.isPresent()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to retrieve Town bank"));
                return CommandResult.empty();
            }
            final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
            final double price = t.getType().getClaimPrice(type);
            final ResultType result = EconomyUtil.withdraw(accOpt.get(), es.getDefaultCurrency(), BigDecimal.valueOf(price));
            if (result == ResultType.ACCOUNT_NO_FUNDS) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Not enough money"));
                return CommandResult.empty();
            }
            if (result != ResultType.SUCCESS) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Error while paying: ", result.name()));
                return CommandResult.empty();
            }
        }

        final boolean claimed = t.claim(source.getLocation(), type, name);
        if (!claimed) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Error while claiming"));
            return CommandResult.empty();
        }

        final Vector3i cp = source.getLocation().getChunkPosition();
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Claimed: [", cp.getX(), ", ", cp.getZ(), "]"));

        return CommandResult.success();
    }
}
