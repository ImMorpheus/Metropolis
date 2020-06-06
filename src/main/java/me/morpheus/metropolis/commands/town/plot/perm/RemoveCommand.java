package me.morpheus.metropolis.commands.town.plot.perm;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;

public class RemoveCommand extends AbstractHomeTownCommand {

    public RemoveCommand() {
        super(
                GenericArguments.allOf(
                        MPGenericArguments.guardedCatalog(Flag.class, flag -> "metropolis.commands.town.plot.perm.remove." + flag.getId(), Text.of("flag"))
                ),
                InputTokenizer.spaceSplitString(),
                "metropolis.commands.town.plot.perm.remove.base",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final Collection<Flag> flags = context.getAll("flag");

        for (Flag flag : flags) {
            plot.removePermission(flag);
        }
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Plot permissions have been updated"));

        return CommandResult.success();
    }
}
