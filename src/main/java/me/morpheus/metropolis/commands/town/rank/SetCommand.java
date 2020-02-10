package me.morpheus.metropolis.commands.town.rank;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Optional;

class SetCommand extends AbstractCitizenCommand {

    SetCommand() {
        super(
                GenericArguments.seq(
                        GenericArguments.onlyOne(MPGenericArguments.catalog(Rank.class, Text.of("rank"))),
                        MPGenericArguments.citizen(Text.of("citizens"))
                ),
                InputTokenizer.quotedStrings(false),
                Metropolis.ID + ".commands.town.rank.set",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Collection<User> citizens = context.getAll("citizens");
        final Rank rank = context.requireOne("rank");

        for (User user : citizens) {
            final Optional<CitizenData> cdOpt = user.get(CitizenData.class);
            if (!cdOpt.isPresent()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, NameUtil.getDisplayName(user), " doesn't have a town"));
            } else if (cdOpt.get().town().get().intValue() != cd.town().get().intValue()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, NameUtil.getDisplayName(user), " is not part of your town"));
            } else {
                cdOpt.get().set(CitizenKeys.RANK, rank);
                user.offer(cdOpt.get());
            }
        }

        return CommandResult.success();
    }
}
