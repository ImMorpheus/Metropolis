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

    private final boolean sourceIfEmpty;

    TownCommandElement(@Nullable Text key) {
        this(key, false);
    }

    TownCommandElement(@Nullable Text key, boolean sourceIfEmpty) {
        super(key);
        this.sourceIfEmpty = sourceIfEmpty;

    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (!args.hasNext()) {
            if (this.sourceIfEmpty) {
                return getTown(source);
            }
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


    @Nullable
    private Town getTown(CommandSource source) {
        if (source instanceof Player) {
            final Optional<CitizenData> cdOpt = ((Player) source).get(CitizenData.class);
            if (cdOpt.isPresent()) {
                final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
                return ts.get(cdOpt.get().town().get().intValue()).orElse(null);
            }
        }
        return null;
    }

}
