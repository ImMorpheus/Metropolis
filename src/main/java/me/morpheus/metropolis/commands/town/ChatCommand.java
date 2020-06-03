package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class ChatCommand extends AbstractCitizenCommand {

    ChatCommand() {
        super(
                TownDispatcher.PERM + ".chat.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final boolean current = cd.chat().get().booleanValue();
        cd.set(CitizenKeys.CHAT, !current);
        source.offer(cd);
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "town chat set to ", !current));
        return CommandResult.success();
    }
}
