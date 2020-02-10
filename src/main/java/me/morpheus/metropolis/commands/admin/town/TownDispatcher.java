package me.morpheus.metropolis.commands.admin.town;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class TownDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new JoinCommand(), "join");
        register(new LeaveCommand(), "leave");
        register(new TakeoverCommand(), "takeover");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
