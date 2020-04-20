package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
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

class SpawnCommand extends AbstractHomeTownCommand {

    public SpawnCommand() {
        super(
                Metropolis.ID + ".commands.town.set.spawn",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        PlotType type = plot.getType();
        if (type != PlotTypes.PLOT && type != PlotTypes.HOMEBLOCK) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You can't set the spawn here. This is a ", type.getName()));
            return CommandResult.empty();
        }

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        ps.get(t.getSpawn()).ifPresent(plotData -> plotData.setType(PlotTypes.PLOT));

        t.setSpawn(source.getLocation());
        plot.setType(PlotTypes.HOMEBLOCK);

        t.sendMessage(Text.of("Town spawn set to ", t.getSpawn().getBlockPosition(), " in ", t.getSpawn().getExtent().getName()));

        return CommandResult.success();
    }
}
