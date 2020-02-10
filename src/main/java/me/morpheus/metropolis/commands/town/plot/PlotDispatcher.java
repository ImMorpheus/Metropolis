package me.morpheus.metropolis.commands.town.plot;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import me.morpheus.metropolis.commands.town.plot.perm.PermDispatcher;
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
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
