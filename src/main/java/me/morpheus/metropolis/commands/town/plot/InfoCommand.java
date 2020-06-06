package me.morpheus.metropolis.commands.town.plot;

import com.flowpowered.math.vector.Vector3i;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class InfoCommand extends AbstractPlayerCommand {

    InfoCommand() {
        super(
                Metropolis.ID + ".commands.town.plot.info.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context) throws CommandException {
        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        final Optional<Plot> plotOpt = ps.get(source.getLocation());

        if (!plotOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This chunk is not claimed"));
            return CommandResult.empty();
        }

        final Plot plot = plotOpt.get();
        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        final Town t = ts.get(plot.getTown()).get();
        final Vector3i cp = source.getLocation().getChunkPosition();

        final Text owner = plot.getOwner().isPresent() ? Sponge.getServiceManager().provideUnchecked(UserStorageService.class)
                .get(plot.getOwner().get())
                .map(NameUtil::getDisplayName)
                .orElse(Text.of("None")) : Text.of("None");

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, cp.getX(), ", ", cp.getZ(), TextColors.GOLD, "]"))
                .contents(
                        Text.of(TextColors.DARK_GREEN, "Town: ", TextColors.GREEN, t.getName()),
                        Text.of(TextColors.DARK_GREEN, "Owner: ", TextColors.GREEN, owner),
                        Text.of(TextColors.DARK_GREEN, "Name: ", TextColors.GREEN, plot.getName()),
                        Text.of(TextColors.DARK_GREEN, "Type: ", TextColors.GREEN, plot.getType().getName()),
                        Text.of(TextColors.DARK_GREEN, "Price: ", TextColors.GREEN, plot.getPrice()),
                        Text.of(TextColors.DARK_GREEN, "Rent: ", TextColors.GREEN, plot.getRent()),
                        Text.of(TextColors.DARK_GREEN, "Mobspawn: ", (plot.hasMobSpawn() ? Text.of(TextColors.GREEN, "On") : Text.of(TextColors.RED, "Off")))
                )
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
