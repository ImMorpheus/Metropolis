package me.morpheus.metropolis.commands.town.citizen;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class InfoCommand extends AbstractMPCommand {

    InfoCommand() {
        super(
                GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                MPGenericArguments.exactlyOne(MPGenericArguments.citizen(Text.of("citizen"))),
                                CitizenDispatcher.PERM + ".info.other"
                        )
                ),
                MinimalInputTokenizer.INSTANCE,
                CitizenDispatcher.PERM + ".info.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        @Nullable final User user = context.<User>getOne("citizen")
                .orElseGet(() -> {
                    if (!(source instanceof Player)) {
                        return null;
                    }

                    final Optional<CitizenData> cdOpt = ((Player) source).get(CitizenData.class);
                    if (!cdOpt.isPresent()) {
                        return null;
                    }
                    return (User) source;
                });

        if (user == null) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        final CitizenData cd = user.get(CitizenData.class).get();
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        final Town t = ts.get(cd.town().get().intValue()).get();

        final List<Text> message = new ArrayList<>(4);
        message.add(Text.of(TextColors.DARK_GREEN, "Town: ", TextColors.GREEN, t.getName()));
        message.add(Text.of(TextColors.DARK_GREEN, "Rank: ", TextColors.GREEN, cd.rank().get().getName()));
        if (source.hasPermission(Metropolis.ID + ".commands.town.citizen.info.friends")) {
            final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
            final Set<Text> friends = cd.friends().get().stream()
                    .map(uuid -> uss.get(uuid)
                            .map(u -> u.isOnline() ? Text.of(TextColors.GREEN, NameUtil.getDisplayName(u)) : Text.of(TextColors.GRAY, NameUtil.getDisplayName(u)))
                            .orElse(Text.of()))
                    .collect(Collectors.toSet());

            message.add(Text.of(TextColors.DARK_GREEN, "Friends: ", Text.joinWith(Text.of(","), friends)));
        }
        if (source.hasPermission(Metropolis.ID + ".commands.town.citizen.info.joined")) {
            final String joined = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault()).format(cd.joined().get());
            message.add(Text.of(TextColors.DARK_GREEN, "Joined: ", TextColors.GREEN, joined));
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, NameUtil.getDisplayName(user), TextColors.GOLD, "]"))
                .contents(message)
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
