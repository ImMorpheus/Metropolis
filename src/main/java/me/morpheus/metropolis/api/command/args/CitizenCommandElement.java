package me.morpheus.metropolis.api.command.args;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.TownService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class CitizenCommandElement extends CommandElement {

    private final CommandElement user;

    CitizenCommandElement(Text key) {
        super(key);
        this.user = GenericArguments.user(key);
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        this.user.parse(source, args, context);
        final Collection<User> players = context.getAll(getKey());
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        for (User u : players) {
            if (!u.get(CitizenData.class).filter(cd -> ts.exist(cd.town().get().intValue())).isPresent()) {
                throw args.createError(Text.of(u.getName(), " is not part of any town"));
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
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        return this.user.complete(src, args, context).stream()
                .filter(s -> uss.get(s).isPresent())
                .map(s -> uss.get(s).get())
                .filter(u -> u.get(CitizenData.class).isPresent())
                .map(User::getName)
                .collect(Collectors.toList());
    }
}

