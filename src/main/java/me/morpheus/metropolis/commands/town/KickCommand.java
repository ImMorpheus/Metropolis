package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;

class KickCommand extends AbstractCitizenCommand {

    KickCommand() {
        super(
                GenericArguments.allOf(MPGenericArguments.citizen(Text.of("citizens"))),
                InputTokenizer.spaceSplitString(),
                TownDispatcher.PERM + ".kick.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Collection<User> citizens = context.getAll("citizens");

        final Text sourceName = NameUtil.getDisplayName(source);
        for (User citizen : citizens) {
            final CitizenData targetCd = citizen.get(CitizenData.class).get();
            final Text citizenName = NameUtil.getDisplayName(citizen);

            if (targetCd.town().get().intValue() != t.getId()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, citizenName, " is not part of your town"));
            } else if (!targetCd.rank().get().canLeave()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, citizenName, " can't leave the town"));
            } else if (!t.kick(citizen.getUniqueId())) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to kick ", citizenName));
            } else {
                t.sendMessage(Text.of(citizenName, " was kicked from the town by ", sourceName));
                if (citizen.isOnline()) {
                    citizen.getPlayer().get().sendMessage(TextUtil.watermark(TextColors.AQUA, "You were kicked from the town by ", sourceName));
                }
            }
        }

        return CommandResult.success();
    }
}
