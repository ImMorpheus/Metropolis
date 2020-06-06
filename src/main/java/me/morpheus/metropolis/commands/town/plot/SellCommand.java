package me.morpheus.metropolis.commands.town.plot;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

public class SellCommand extends AbstractHomeTownCommand {

    public SellCommand() {
        super(
                MPGenericArguments.positiveBigDecimal(Text.of("price")),
                MinimalInputTokenizer.INSTANCE,
                "metropolis.commands.town.plot.sell.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final BigDecimal price = context.requireOne("price");

        plot.setForSale(true);
        plot.setPrice(price.doubleValue());
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Plot price set to ", price));

        return CommandResult.success();
    }
}
