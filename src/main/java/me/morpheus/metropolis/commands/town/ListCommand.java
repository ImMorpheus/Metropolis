package me.morpheus.metropolis.commands.town;

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

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends AbstractMPCommand {

    public ListCommand() {
        super(
                "metropolis.commands.town.list.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        final List<Text> towns = Sponge.getServiceManager().provideUnchecked(TownService.class).towns()
                .map(Town::getName)
                .collect(Collectors.toList());

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Towns", TextColors.GOLD, "]"))
                .contents(Text.of(TextColors.AQUA, Text.joinWith(Text.of(", "), towns)))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
