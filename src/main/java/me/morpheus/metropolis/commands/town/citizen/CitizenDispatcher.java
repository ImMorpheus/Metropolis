package me.morpheus.metropolis.commands.town.citizen;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class CitizenDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new InfoCommand(), "info");
        register(new OnlineCommand(), "online");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
