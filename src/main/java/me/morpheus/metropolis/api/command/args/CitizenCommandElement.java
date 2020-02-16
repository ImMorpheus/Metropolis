package me.morpheus.metropolis.api.command.args;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.NameUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CitizenCommandElement extends CommandElement {

    CitizenCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (!args.hasNext()) {
            return null;
        }

        final List<User> users = new ArrayList<>(args.size());

        while (args.hasNext()) {
            final String next = args.next();

            final User u = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(next)
                    .filter(p -> p.get(CitizenData.class).filter(cd -> Sponge.getServiceManager().provideUnchecked(TownService.class).exist(cd.town().get().intValue())).isPresent())
                    .orElseThrow(() -> args.createError(Text.of("Invalid citizen!")));

            users.add(u);
        }
        return Collections.unmodifiableList(users);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        final String arg = args.nextIfPresent().orElse("");
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);

        return uss.getAll().stream()
                .filter(gp -> uss.get(gp).isPresent())
                .map(gp -> uss.get(gp).get())
                .filter(u -> u.get(CitizenData.class).isPresent())
                .map(u -> NameUtil.getDisplayName(u).toPlain())
                .filter(name -> name.startsWith(arg))
                .collect(Collectors.toList());
    }
}

