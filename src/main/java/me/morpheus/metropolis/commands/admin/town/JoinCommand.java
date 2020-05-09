package me.morpheus.metropolis.commands.admin.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.rank.Ranks;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
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

class JoinCommand extends AbstractPlayerCommand {

    JoinCommand() {
        super(
                MPGenericArguments.exactlyOne(MPGenericArguments.town(Text.of("town"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.admin.town.join",
                Text.of()
        );
    }
    @Override
    protected CommandResult process(Player source, CommandContext context) throws CommandException {
        final Town town = context.requireOne("town");
        final CitizenData cd = Sponge.getDataManager().getManipulatorBuilder(CitizenData.class).get().create();
        cd.set(CitizenKeys.TOWN, town.getId());
        cd.set(CitizenKeys.RANK, Ranks.CITIZEN);
        source.offer(cd);
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "You are now part of ", town.getName()));

        return CommandResult.success();
    }
}
