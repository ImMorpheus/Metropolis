package me.morpheus.metropolis.commands.town.plot;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

class SellCommand extends AbstractHomeTownCommand {

    SellCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("price"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.plot.sell",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, PlotData pd) throws CommandException {
        final double price = context.requireOne("price");

        pd.set(PlotKeys.FORSALE, true);
        pd.set(PlotKeys.PRICE, price);

        return CommandResult.success();
    }
}
