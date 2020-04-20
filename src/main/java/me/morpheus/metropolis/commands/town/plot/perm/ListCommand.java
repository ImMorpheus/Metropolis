package me.morpheus.metropolis.commands.town.plot.perm;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class ListCommand extends AbstractHomeTownCommand {

    public ListCommand() {
        super(
                Metropolis.ID + ".commands.town.plot.perm.list",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final Collection<Rank> ranks = Sponge.getRegistry().getAllOf(Rank.class);
        final Collection<Flag> flags = Sponge.getRegistry().getAllOf(Flag.class);

        final List<Text> perms = new ArrayList<>(flags.size());
        for (Flag flag : flags) {
            final int pp = plot.getPermission(flag);
            if (pp != Byte.MIN_VALUE) {
                final List<String> allowed = ranks.stream()
                        .filter(rank -> rank.getPermission(flag) >= pp)
                        .map(CatalogType::getName)
                        .collect(Collectors.toList());
                perms.add(Text.of(TextColors.AQUA, flag.getName(), ": ", allowed));
            }
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Perms", TextColors.GOLD, "]"))
                .contents(perms)
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
