package me.morpheus.metropolis.commands.town.plot.set;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.TownDispatcher;
import me.morpheus.metropolis.commands.town.plot.PlotDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class SetDispatcher extends AbstractCommandDispatcher {

    public static final String PERM = PlotDispatcher.PERM + ".set";

    @Override
    public void registerDefaults() {
        register(new MobSpawnCommand(), "mobspawn");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
