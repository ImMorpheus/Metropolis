package me.morpheus.metropolis.commands.town.plot.perm;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.rank.Rank;
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

class SetCommand extends AbstractHomeTownCommand {

    SetCommand() {
        super(
                GenericArguments.seq(
                        GenericArguments.onlyOne(MPGenericArguments.catalog(Flag.class, Text.of("flag"))),
                        GenericArguments.onlyOne(MPGenericArguments.catalog(Rank.class, Text.of("rank")))
                ),
                InputTokenizer.spaceSplitString(),
                Metropolis.ID + ".commands.town.plot.perm.set",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final Flag flag = context.requireOne("flag");
        final Rank rank = context.requireOne("rank");

        plot.addPermission(flag, rank.getPermission(flag));

        return CommandResult.success();
    }
}
