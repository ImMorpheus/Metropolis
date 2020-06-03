package me.morpheus.metropolis.commands.town.friend;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

class AddCommand extends AbstractCitizenCommand {

    AddCommand() {
        super(
                GenericArguments.allOf(GenericArguments.user(Text.of("friends"))),
                InputTokenizer.spaceSplitString(),
                FriendDispatcher.PERM + ".add.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Collection<User> users = context.getAll("friends");

        for (User user : users) {
            cd.transform(CitizenKeys.FRIENDS, friends -> {
                friends.add(user.getUniqueId());
                return friends;
            });
        }

        final DataTransactionResult result = source.offer(cd);
        if (!result.isSuccessful()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to update your friend list"));
            return CommandResult.empty();
        }
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Your friend list has been updated"));
        return CommandResult.success();
    }
}
