package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.visibility.Visibility;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class VisibilityCommand extends AbstractCitizenCommand {

    VisibilityCommand() {
        super(
                MPGenericArguments.exactlyOne(
                        MPGenericArguments.guardedCatalog(Visibility.class, v -> SetDispatcher.PERM + ".visibility." + v.getId(), Text.of("visibility"))
                ),
                MinimalInputTokenizer.INSTANCE,
                SetDispatcher.PERM + ".visibility.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Visibility visibility = context.requireOne("visibility");

        t.setVisibility(visibility);
        t.sendMessage(Text.of("Town visibility set to ", visibility.getName()));

        return CommandResult.success();
    }
}
