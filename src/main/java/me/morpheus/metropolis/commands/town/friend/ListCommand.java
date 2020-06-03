package me.morpheus.metropolis.commands.town.friend;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

class ListCommand extends AbstractCitizenCommand {

    ListCommand() {
        super(
                FriendDispatcher.PERM + ".list.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
        final Optional<Set<UUID>> friendsOpt = cd.get(CitizenKeys.FRIENDS);
        if (!friendsOpt.isPresent() || friendsOpt.get().isEmpty()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have any friends"));
            return CommandResult.empty();
        }

        final Set<Text> friends = friendsOpt.get().stream()
                .map(uuid -> uss.get(uuid)
                        .map(user -> user.isOnline() ? Text.of(TextColors.GREEN, NameUtil.getDisplayName(user)) : Text.of(TextColors.GRAY, NameUtil.getDisplayName(user)))
                        .orElse(Text.of()))
                .collect(Collectors.toSet());

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Friends", TextColors.GOLD, "]"))
                .contents(Text.joinWith(Text.of(","), friends))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
