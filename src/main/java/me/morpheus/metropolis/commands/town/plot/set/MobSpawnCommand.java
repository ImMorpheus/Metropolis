package me.morpheus.metropolis.commands.town.plot.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class MobSpawnCommand extends AbstractHomeTownCommand {

    MobSpawnCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.bool(Text.of("value"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.plot.set.mobspawn",
                Text.of("Turn on/off mobspawning in the plot.")
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final boolean flag = context.requireOne("value");

        plot.setMobSpawn(flag);
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Mobspawn ", (flag ? "on" : "off"), " in plot"));

        return CommandResult.success();
    }
}
