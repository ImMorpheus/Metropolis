package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.Upgrade;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

class UpgradeCommand extends AbstractCitizenCommand {

    UpgradeCommand() {
        super(
                GenericArguments.optional(
                        GenericArguments.onlyOne(MPGenericArguments.catalog(Upgrade.class, Text.of("upgrade")))
                ),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.upgrade",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Optional<Upgrade> upgradeOpt = context.getOne("upgrade");
        if (!upgradeOpt.isPresent()) {
            final Collection<Text> upgrades = Sponge.getRegistry().getAllOf(Upgrade.class).stream()
                    .filter(upgrade -> upgrade.getRequiredTownTypes().contains(t.getType()))
                    .map(upgrade -> Text.of(upgrade.getName()))
                    .collect(Collectors.toList());

            if (upgrades.isEmpty()) {
                source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Your town has no upgrade"));
                return CommandResult.empty();
            }

            PaginationList.builder()
                    .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Upgrades", TextColors.GOLD, "]"))
                    .contents(Text.of(TextColors.AQUA, Text.joinWith(Text.NEW_LINE, upgrades)))
                    .padding(Text.of(TextColors.GOLD, "-"))
                    .sendTo(source);
            return CommandResult.success();
        }
        final Upgrade upgrade = upgradeOpt.get();
        final boolean success = t.upgrade(upgrade);
        if (!success) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Upgrade failed"));
            return CommandResult.empty();
        }
        t.sendMessage(Text.of(upgrade.getName(), " upgrade completed"));
        return CommandResult.success();
    }
}
