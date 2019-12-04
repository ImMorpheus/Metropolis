package me.morpheus.metropolis.commands.admin.town;

import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.rank.Ranks;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

class JoinCommand extends AbstractPlayerCommand {

    JoinCommand() {
        super(GenericArguments.onlyOne(MPGenericArguments.town(Text.of("town"))), InputTokenizer.rawInput());
    }
    @Override
    protected CommandResult process(Player source, CommandContext context) throws CommandException {
        Town town = context.requireOne("town");
        boolean success = town.accept(source.getUniqueId(), Ranks.CITIZEN);

        if (!success) {
            return CommandResult.empty();
        }
        return CommandResult.success();
    }

    @Override
    protected boolean testPermission(Player player) {
        return player.hasPermission("metropolis.command.admin.join");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }
}
