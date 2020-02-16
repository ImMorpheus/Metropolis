package me.morpheus.metropolis.api.command.args;

import me.morpheus.metropolis.util.NameUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class VisiblePlayerCommandElement extends CommandElement {

    VisiblePlayerCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (!args.hasNext()) {
            return null;
        }

        final List<Player> players = new ArrayList<>(args.size());

        while (args.hasNext()) {
            final String next = args.next();

            final Player pl = Sponge.getServer().getPlayer(next)
                    .filter(p -> !p.get(InvisibilityData.class).filter(id -> id.vanish().get().booleanValue()).isPresent())
                    .orElseThrow(() -> args.createError(Text.of("Invalid player!")));

            players.add(pl);
        }
        return Collections.unmodifiableList(players);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        final String arg = args.nextIfPresent().orElse("");

        return Sponge.getServer().getOnlinePlayers().stream()
                .filter(p -> !p.get(Keys.VANISH).orElse(false))
                .map(p -> NameUtil.getDisplayName(p).toPlain())
                .filter(name -> name.startsWith(arg))
                .collect(Collectors.toList());
    }
}
