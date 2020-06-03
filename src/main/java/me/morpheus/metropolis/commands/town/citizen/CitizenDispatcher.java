package me.morpheus.metropolis.commands.town.citizen;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.TownDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class CitizenDispatcher extends AbstractCommandDispatcher {

    public static final String PERM = TownDispatcher.PERM + ".citizen";

    @Override
    public void registerDefaults() {
        register(new InfoCommand(), "info");
        register(new ListCommand(), "list");
        register(new OnlineCommand(), "online");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
