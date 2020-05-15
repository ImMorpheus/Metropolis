package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.TownData;
import me.morpheus.metropolis.api.town.Town;

import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.town.visibility.Visibilities;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

class InfoCommand extends AbstractMPCommand {

    InfoCommand() {
        super(
                GenericArguments.requiringPermission(
                        MPGenericArguments.town(Text.of("town")),
                        Metropolis.ID + ".commands.town.info.other"
                ),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.info.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        @Nullable final Town t = context.<Town>getOne("town")
                .orElseGet(() -> {
                    if (!(source instanceof Player)) {
                        return null;
                    }

                    final Optional<CitizenData> cdOpt = ((Player) source).get(CitizenData.class);
                    if (!cdOpt.isPresent()) {
                        return null;
                    }

                    final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
                    return ts.get(cdOpt.get().town().get().intValue())
                            .orElseThrow(() -> new RuntimeException("Corrupted CitizenData (invalid town)"));
                });

        if (t == null) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, t.getName(), TextColors.GOLD, "]"))
                .contents(t.getTownScreen(source))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
