package me.morpheus.metropolis.commands.town.plot.set;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class SetDispatcher extends AbstractCommandDispatcher {
    @Override
    public void registerDefaults() {
        register(new MobSpawnCommand(), "mobspawn");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
