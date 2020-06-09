package me.morpheus.metropolis.plot.commands;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.plot.SimplePlotService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class IgnoreClaimCommand extends AbstractPlayerCommand {

    private final SimplePlotService ps;

    public IgnoreClaimCommand(SimplePlotService ps) {
        super(
                MPGenericArguments.exactlyOne(GenericArguments.bool(Text.of("toggle"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.admin.ignoreclaims",
                Text.of()
        );
        this.ps = ps;
    }

    @Override
    public CommandResult process(Player source, CommandContext context) throws CommandException {
        final Boolean toggle = context.requireOne("toggle");
        if (toggle.booleanValue()) {
            if (this.ps.getIgnoreClaims().add(source.getUniqueId())) {
                source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Ignoreclaims enabled"));
            } else {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Ignoreclaims already enabled"));
                return CommandResult.empty();
            }
        } else {
            if (this.ps.getIgnoreClaims().remove(source.getUniqueId())) {
                source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Ignoreclaims disabled"));
            } else {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Ignoreclaims already disabled"));
                return CommandResult.empty();
            }
        }
        return CommandResult.success();
    }
}
