package me.morpheus.metropolis.commands.admin.plot;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class DisownCommand extends AbstractPlayerCommand {

    DisownCommand() {
        super(
                Metropolis.ID + ".commands.admin.plot.disown",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context) throws CommandException {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(source.getLocation());

        if (!plotOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This plot is not claimed"));
            return CommandResult.empty();
        }

        plotOpt.get().setOwner(null);
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "The plot owner has been removed"));

        return CommandResult.success();
    }
}
