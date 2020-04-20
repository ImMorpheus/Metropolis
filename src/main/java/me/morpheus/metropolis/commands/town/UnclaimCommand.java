package me.morpheus.metropolis.commands.town;

import com.flowpowered.math.vector.Vector3i;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class UnclaimCommand extends AbstractHomeTownCommand {

    public UnclaimCommand() {
        super(
                Metropolis.ID + ".commands.town.unclaim",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final boolean unclaimed = t.unclaim(source.getLocation());
        if (!unclaimed) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Error while unclaiming"));
            return CommandResult.empty();
        }

        final Vector3i cp = source.getLocation().getChunkPosition();
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Unclaimed: [", cp.getX(), ",", cp.getZ(), "]"));

        return CommandResult.success();
    }
}
