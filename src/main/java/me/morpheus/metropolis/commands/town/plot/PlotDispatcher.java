package me.morpheus.metropolis.commands.town.plot;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.plot.perm.PermDispatcher;
import me.morpheus.metropolis.commands.town.plot.set.SetDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class PlotDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new InfoCommand(), "info");
        register(new BuyCommand(), "buy");
        register(new SellCommand(), "sell");
        register(new NameCommand(), "name");

        registerManager(new PermDispatcher(), "perm");
        registerManager(new SetDispatcher(), "set");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
