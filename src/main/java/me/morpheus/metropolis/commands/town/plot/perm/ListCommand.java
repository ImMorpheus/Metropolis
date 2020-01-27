package me.morpheus.metropolis.commands.town.plot.perm;

import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class ListCommand extends AbstractHomeTownCommand {

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, PlotData pd) throws CommandException {
        final Text perms = Sponge.getRegistry().getAllOf(Flag.class).stream()
                .map(f -> Text.of(f.getName(), ": ", pd.getPermission(f)))
                .reduce(Text.EMPTY, (p1, p2) -> Text.NEW_LINE);


        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Perms", TextColors.GOLD, "]"))
                .contents(Text.of(TextColors.AQUA, perms))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }

    @Override
    protected boolean testPermission(Player source, CitizenData cd, PlotData pd) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("List perms"));
    }
}
