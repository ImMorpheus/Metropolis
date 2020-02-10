package me.morpheus.metropolis.commands.admin.plot;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class PlotDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new DisownCommand(), "disown");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
