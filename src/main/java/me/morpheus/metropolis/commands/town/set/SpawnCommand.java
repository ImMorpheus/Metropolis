package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

class SpawnCommand extends AbstractHomeTownCommand {

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, PlotData pd) throws CommandException {
        t.setSpawn(source.getLocation());
        t.sendMessage(TextUtil.watermark("Town spawn set to ", t.getSpawn().getBlockPosition(), " in ", t.getSpawn().getExtent().getName()));

        return CommandResult.success();
    }

    @Override
    protected boolean testPermission(Player source, CitizenData cd, PlotData pd) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Sets the town spawn"));
    }
}
