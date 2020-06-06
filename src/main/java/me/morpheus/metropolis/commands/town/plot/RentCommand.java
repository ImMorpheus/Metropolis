package me.morpheus.metropolis.commands.town.plot;

import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;

public class RentCommand extends AbstractHomeTownCommand {

    public RentCommand() {
        super(
                MPGenericArguments.positiveBigDecimal(Text.of("rent")),
                MinimalInputTokenizer.INSTANCE,
                "metropolis.commands.town.plot.rent.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final BigDecimal price = context.requireOne("rent");

        plot.setRent(price.doubleValue());
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Plot rent set to ", price));

        return CommandResult.success();
    }
}
