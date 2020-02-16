package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
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

class PvPCommand extends AbstractCitizenCommand {

    PvPCommand() {
        super(
                GenericArguments.onlyOne(MPGenericArguments.catalog(PvPOption.class, Text.of("option"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.set.pvp",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final PvPOption option = context.requireOne("option");

        t.setPvP(option);
        t.sendMessage(TextUtil.watermark(TextColors.AQUA, "Town PvP set to ", option.getName()));

        return CommandResult.success();
    }
}
