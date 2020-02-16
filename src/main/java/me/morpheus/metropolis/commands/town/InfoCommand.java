package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.TownData;
import me.morpheus.metropolis.api.town.Town;

import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.town.visibility.Visibilities;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

class InfoCommand extends AbstractMPCommand {

    InfoCommand() {
        super(
                MPGenericArguments.townOrHomeTown(Text.of("townOrHomeTown")),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.info",
                Text.of()
        );
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        final Optional<Town> tOpt = context.getOne("townOrHomeTown");

        if (!tOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        final Town t = tOpt.get();

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, t.getName(), TextColors.GOLD, "]"))
                .contents(t.getTownScreen(source))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
