package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class VisiblePlayerCommandElement extends CommandElement {

    private final CommandElement player;

    VisiblePlayerCommandElement(Text key) {
        super(key);
        this.player = GenericArguments.player(key);
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        this.player.parse(source, args, context);
        final Collection<Player> players = context.getAll(getKey());
        for (Player p : players) {
            if (p.get(InvisibilityData.class).filter(id -> id.vanish().get().booleanValue()).isPresent()) {
                throw args.createError(Text.of("No values matching pattern", p.getName(), " present for ", getKey(), "!"));
            }
        }
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        throw args.createError(Text.of("How ?"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return this.player.complete(src, args, context).stream()
                .filter(s -> Sponge.getServer().getPlayer(s).isPresent())
                .map(s -> Sponge.getServer().getPlayer(s).get())
                .filter(p -> !p.get(Keys.VANISH).orElse(false))
                .map(User::getName)
                .collect(Collectors.toList());
    }
}
