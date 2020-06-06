package me.morpheus.metropolis.commands.town.plot.perm;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.rank.Rank;
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

import java.util.Collection;
import java.util.Optional;

public class SetCommand extends AbstractHomeTownCommand {

    public SetCommand() {
        super(
                GenericArguments.seq(
                        MPGenericArguments.exactlyOne(
                                MPGenericArguments.guardedCatalog(Rank.class, rank -> "metropolis.commands.town.plot.perm.set.rank." + rank.getId(), Text.of("rank"))
                        ),
                        GenericArguments.allOf(
                                MPGenericArguments.guardedCatalog(Flag.class, flag -> "metropolis.commands.town.plot.perm.set.flag." + flag.getId(), Text.of("flag"))
                        )
                ),
                InputTokenizer.spaceSplitString(),
                "metropolis.commands.town.plot.perm.set.base",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final Rank rank = context.requireOne("rank");
        final Collection<Flag> flags = context.getAll("flag");

        for (Flag flag : flags) {
            plot.addPermission(flag, rank.getPermission(flag));
        }
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Plot permissions have been updated"));

        return CommandResult.success();
    }
}
