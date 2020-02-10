package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class ChatCommand extends AbstractCitizenCommand {

    public ChatCommand() {
        super(
                Metropolis.ID + ".commands.town.chat",
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
