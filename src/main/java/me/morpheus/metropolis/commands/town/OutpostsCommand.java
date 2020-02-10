package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class OutpostsCommand extends AbstractCitizenCommand {

    public OutpostsCommand() {
        super(
                Metropolis.ID + ".commands.town.outposts",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Optional<OutpostData> odOpt = t.get(OutpostData.class);

        if (!odOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Your town has no outpost"));
            return CommandResult.empty();
        }

        List<Text> outposts = new ArrayList<>(odOpt.get().outposts().size());
        for (Map.Entry<String, Location<World>> entry : odOpt.get().outposts().get().entrySet()) {
            outposts.add(Text.of("[", entry.getKey(), "] ", entry.getValue().getBlockPosition(), " (", entry.getValue().getExtent().getName(), ")"));
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Outposts", TextColors.GOLD, "]"))
                .contents(Text.of(TextColors.AQUA, Text.joinWith(Text.NEW_LINE, outposts)))
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
