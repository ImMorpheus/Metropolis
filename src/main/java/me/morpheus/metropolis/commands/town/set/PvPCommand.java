package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.api.command.args.MPGenericArguments;
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
        super(GenericArguments.onlyOne(MPGenericArguments.catalog(PvPOption.class, Text.of("toggle"))), InputTokenizer.rawInput());
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final PvPOption toggle = context.requireOne("toggle");

        t.setPvP(toggle);
        t.sendMessage(TextUtil.watermark(TextColors.AQUA, "Town PvP set to ", toggle.getName()));

        return CommandResult.success();
    }

    @Override
    public boolean testPermission(Player source, CitizenData cd) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of(""));
    }
}
