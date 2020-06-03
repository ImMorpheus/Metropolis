package me.morpheus.metropolis.commands.town.citizen;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class OnlineCommand extends AbstractMPCommand {

    OnlineCommand() {
        super(
                GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                MPGenericArguments.town(Text.of("town")),
                                CitizenDispatcher.PERM + ".online.other"
                        )
                ),
                MinimalInputTokenizer.INSTANCE,
                CitizenDispatcher.PERM + ".online.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        @Nullable final Town town = context.<Town>getOne("town")
                .orElseGet(() -> {
                    if (!(source instanceof Player)) {
                        return null;
                    }

                    final Optional<CitizenData> cdOpt = ((Player) source).get(CitizenData.class);
                    if (!cdOpt.isPresent()) {
                        return null;
                    }

                    final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
                    return ts.get(cdOpt.get().town().get().intValue())
                            .orElseThrow(() -> new RuntimeException("Corrupted CitizenData (invalid town)"));
                });

        if (town == null) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        final Map<Rank, List<Text>> map = Sponge.getServer().getOnlinePlayers().stream()
                .filter(p -> p.get(CitizenKeys.TOWN).filter(i -> i.intValue() == town.getId()).isPresent())
                .filter(p -> !p.get(Keys.VANISH).orElse(false))
                .collect(Collectors.groupingBy(
                        u -> u.get(CitizenData.class).get().rank().get(),
                        Reference2ObjectOpenHashMap::new,
                        Collectors.mapping(user -> user.isOnline() ? Text.of(TextColors.GREEN, NameUtil.getDisplayName(user)) : Text.of(TextColors.GRAY, NameUtil.getDisplayName(user)), Collectors.toList()))
                );

        final List<Text> list = new ArrayList<>(map.size());
        for (Map.Entry<Rank, List<Text>> entry : map.entrySet()) {
            list.add(Text.of(TextColors.DARK_GREEN, entry.getKey().getName(), ": ", Text.joinWith(Text.of(", "), entry.getValue())));
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Online citizens", TextColors.GOLD, "]"))
                .contents(list)
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
