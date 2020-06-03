package me.morpheus.metropolis.commands.town.plot.perm;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.plot.PlotDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class PermDispatcher extends AbstractCommandDispatcher {

    public static final String PERM = PlotDispatcher.PERM + ".perm";

    @Override
    public void registerDefaults() {
        register(new RemoveCommand(), "remove");
        register(new SetCommand(), "set");
        register(new ListCommand(), "list");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}

