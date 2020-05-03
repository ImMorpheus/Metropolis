package me.morpheus.metropolis.api.command.args;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class TownCommandElement extends CommandElement {

    TownCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (!args.hasNext()) {
            return null;
        }

        final String name = args.next();

        return Sponge.getServiceManager().provideUnchecked(TownService.class)
                .towns()
                .filter(t -> t.getName().toPlain().equals(name))
                .findAny()
                .orElseThrow(() -> args.createError(Text.of("Invalid town!")));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        final String arg = args.nextIfPresent().orElse("");

        return Sponge.getServiceManager().provideUnchecked(TownService.class)
                .towns()
                .filter(t -> t.getName().toPlain().startsWith(arg))
                .map(t -> t.getName().toPlain())
                .collect(Collectors.toList());
    }

}
