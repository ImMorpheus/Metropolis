package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.Upgrade;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

class UpgradeCommand extends AbstractCitizenCommand {

    UpgradeCommand() {
        super(
                GenericArguments.onlyOne(MPGenericArguments.catalog(Upgrade.class, Text.of("upgrade"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.upgrade",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Upgrade upgrade = context.requireOne("upgrade");
        final boolean success = t.upgrade(upgrade);
        if (!success) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Upgrade failed"));
            return CommandResult.empty();
        }
        t.sendMessage(TextUtil.watermark(TextColors.AQUA, upgrade.getName(), " upgrade completed"));
        return CommandResult.success();
    }
}
