package me.morpheus.metropolis.commands.admin.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.rank.Ranks;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class TakeoverCommand extends AbstractCitizenCommand {

    public TakeoverCommand() {
        super(
                Metropolis.ID + ".commands.admin.town.takeover",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        cd.set(CitizenKeys.RANK, Ranks.MAYOR);
        source.offer(cd);
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "You hijacked ", t.getName()));

        return CommandResult.success();
    }
}

