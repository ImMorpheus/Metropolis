package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.command.AbstractCommandDispatcher;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public final class SetDispatcher extends AbstractCommandDispatcher {

    @Override
    public void registerDefaults() {
        register(new DescriptionCommand(), "description", "desc");
        register(new MotdCommand(), "motd");
        register(new NameCommand(), "name");
        register(new VisibilityCommand(), "visibility");
        register(new PvPCommand(), "pvp");
        register(new SpawnCommand(), "spawn");
        register(new TagCommand(), "tag");
        register(new TaxCommand(), "tax");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
