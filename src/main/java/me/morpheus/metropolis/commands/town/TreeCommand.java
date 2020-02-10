package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractMPCommand;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.dispatcher.Dispatcher;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class TreeCommand extends AbstractMPCommand {

    public TreeCommand() {
        super(
                Metropolis.ID + ".commands.town.tree",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        final CommandMapping town = Sponge.getCommandManager().get("t").get();

        final Collection<Text> commands = getSubCommands(source, "/" + town.getPrimaryAlias(), (Dispatcher) town.getCallable());

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Commands", TextColors.GOLD, "]"))
                .contents(commands)
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();

    }

    private Collection<Text> getSubCommands(CommandSource source, String root, Dispatcher dispatcher) {
        final List<Text> commands = new ArrayList<>();

        final Set<? extends CommandMapping> mappings = dispatcher.getCommands();
        for (CommandMapping mapping : mappings) {
            if (mapping.getCallable() instanceof Dispatcher) {
                commands.addAll(getSubCommands(source, root + " " + mapping.getPrimaryAlias(), (Dispatcher) mapping.getCallable()));
            } else {
                Text text = Text.of(root + " " + mapping.getPrimaryAlias() + " " + mapping.getCallable().getUsage(source).toPlain());
                commands.add(text);
            }
        }
        return commands;
    }
}
