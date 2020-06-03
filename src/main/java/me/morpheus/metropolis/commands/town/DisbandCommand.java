package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class DisbandCommand extends AbstractCitizenCommand {

    DisbandCommand() {
        super(
                TownDispatcher.PERM + ".disband.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final boolean success = t.disband();
        if (!success) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Error while disbanding"));
            return CommandResult.empty();
        }

        final Text broadcast = TextUtil.watermark(TextColors.AQUA, t.getName(), " fell into ruin ");
        Sponge.getServer().getBroadcastChannel().send(broadcast);

        return CommandResult.success();
    }
}
