package me.morpheus.metropolis.commands.town.plot.perm;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class PermDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new SetCommand(), "set");
        register(new ListCommand(), "list");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}

