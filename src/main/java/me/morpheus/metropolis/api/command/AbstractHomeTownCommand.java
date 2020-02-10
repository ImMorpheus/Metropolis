package me.morpheus.metropolis.api.command;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public abstract class AbstractHomeTownCommand extends AbstractCitizenCommand {

    protected AbstractHomeTownCommand(CommandElement args, InputTokenizer tokenizer, String permission, Text description) {
        super(args, tokenizer, permission, description);
    }

    protected AbstractHomeTownCommand(String permission, Text description) {
        super(permission, description);
    }

    @Override
    protected final CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<PlotData> pdOpt = ps.get(source.getLocation());

        if (!pdOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This plot is not claimed"));
            return CommandResult.empty();
        }

        if (pdOpt.get().town().get().intValue() != cd.town().get().intValue()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This plot is claimed by another town"));
            return CommandResult.empty();
        }

        return process(source, context, cd, t, pdOpt.get());
    }

    protected abstract CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, PlotData pd) throws CommandException;

}
