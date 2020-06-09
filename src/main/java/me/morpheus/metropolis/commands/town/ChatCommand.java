package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
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
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

public class ChatCommand extends AbstractCitizenCommand {

    public ChatCommand() {
        super(
                Metropolis.ID + ".commands.town.chat.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        if (source.getMessageChannel() == t.getMessageChannel()) {
            source.setMessageChannel(MessageChannel.TO_ALL);
            source.sendMessage(TextUtil.watermark(TextColors.AQUA, "town chat has been disabled"));
        } else {
            source.setMessageChannel(t.getMessageChannel());
            source.sendMessage(TextUtil.watermark(TextColors.AQUA, "town chat has been enabled"));
        }

        return CommandResult.success();
    }
}
