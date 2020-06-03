package me.morpheus.metropolis.commands.town.invitation;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.TownDispatcher;
import me.morpheus.metropolis.commands.town.plot.PlotDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class InvitationDispatcher extends AbstractCommandDispatcher {

    public static final String PERM = TownDispatcher.PERM + ".invitation";

    @Override
    public void registerDefaults() {
        register(new ListCommand(), "list");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
