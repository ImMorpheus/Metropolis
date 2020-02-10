package me.morpheus.metropolis.commands.town.friend;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class FriendDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new AddCommand(), "add");
        register(new RemoveCommand(), "remove");
        register(new ListCommand(), "list");
        register(new ClearCommand(), "clear");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}

