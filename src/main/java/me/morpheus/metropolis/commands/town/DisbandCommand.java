package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.rank.Ranks;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class DisbandCommand extends AbstractCitizenCommand {

    public DisbandCommand() {
        super(
                Metropolis.ID + ".commands.town.disband",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        t.disband();

        final Text broadcast = TextUtil.watermark(TextColors.AQUA, t.getName(), " fell into ruin ");
        Sponge.getServer().getBroadcastChannel().send(broadcast);

        return CommandResult.success();
    }
}
