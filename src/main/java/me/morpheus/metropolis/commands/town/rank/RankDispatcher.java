package me.morpheus.metropolis.commands.town.rank;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.TownDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class RankDispatcher extends AbstractCommandDispatcher {

    public static final String PERM = TownDispatcher.PERM + ".rank";

    @Override
    public void registerDefaults() {
        register(new SetCommand(), "set");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
