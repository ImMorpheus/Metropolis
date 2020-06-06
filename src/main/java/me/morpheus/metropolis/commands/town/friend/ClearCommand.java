package me.morpheus.metropolis.commands.town.friend;

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
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ClearCommand extends AbstractCitizenCommand {

    public ClearCommand() {
        super(
                "metropolis.commands.town.friend.clear.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        cd.set(CitizenKeys.FRIENDS, null);
        final DataTransactionResult result = source.offer(cd);
        if (!result.isSuccessful()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to clear your friend list"));
            return CommandResult.empty();
        }
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Your friend list has been cleared"));
        return CommandResult.success();
    }
}

