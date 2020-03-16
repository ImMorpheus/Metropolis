package me.morpheus.metropolis.commands.town.plot.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class MobSpawnCommand extends AbstractHomeTownCommand {

    MobSpawnCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.bool(Text.of("value"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.plot.set.mobspawn",
                Text.of("Activates mob spawn in plot.")
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, PlotData pd) throws CommandException {
        final boolean flag = context.requireOne("value");

        pd.set(PlotKeys.MOBSPAWN, flag);
        source.sendMessage(Text.of("Mobspawn " + (flag ? "enabled" : "disabled") + "  in plot"));

        return CommandResult.success();
    }
}