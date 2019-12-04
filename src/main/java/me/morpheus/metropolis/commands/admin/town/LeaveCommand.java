package me.morpheus.metropolis.commands.admin.town;

import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class LeaveCommand extends AbstractCitizenCommand {

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        source.remove(CitizenData.class);
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "You left the town"));
        return CommandResult.success();
    }

    @Override
    public boolean testPermission(Player source, CitizenData cd) {
        return source.hasPermission("metropolis.command.admin.leave");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Leaves a town"));
    }
}
