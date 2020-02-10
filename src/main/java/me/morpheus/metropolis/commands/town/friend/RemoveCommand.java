package me.morpheus.metropolis.commands.town.friend;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

class RemoveCommand extends AbstractCitizenCommand {

    RemoveCommand() {
        super(
                GenericArguments.user(Text.of("friends")),
                InputTokenizer.spaceSplitString(),
                Metropolis.ID + ".commands.town.friend.remove",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        Collection<User> users = context.getAll("friends");

        Set<UUID> friends = cd.friends().get();
        for (User user : users) {
            friends.remove(user.getUniqueId());
        }

        source.offer(CitizenKeys.FRIENDS, friends);

        return CommandResult.success();
    }
}
