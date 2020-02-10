package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.command.AbstractMPCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class ListCommand extends AbstractMPCommand {

    public ListCommand() {
        super(
                Metropolis.ID + ".commands.town.list",
                Text.of()
        );
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        final Text towns = Sponge.getServiceManager().provideUnchecked(TownService.class).towns()
                .map(Town::getName)
                .reduce(Text.EMPTY, (t1, t2) -> Text.of(t1, ", ", t2));

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Towns", TextColors.GOLD, "]"))
                .contents(Text.of(TextColors.AQUA, towns))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
