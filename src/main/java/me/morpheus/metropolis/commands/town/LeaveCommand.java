package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class LeaveCommand extends AbstractCitizenCommand {

    public LeaveCommand() {
        super(
                Metropolis.ID + ".commands.town.leave",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Rank rank = cd.rank().get();

        if (!rank.canLeave()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You can't leave this town"));
            return CommandResult.empty();
        }

        final boolean success = t.kick(source.getUniqueId());

        if (!success) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to leave"));
            return CommandResult.empty();
        }

        t.sendMessage(TextUtil.watermark(TextColors.AQUA, NameUtil.getDisplayName(source), " left the town"));
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "You left the town"));
        return CommandResult.success();
    }
}
