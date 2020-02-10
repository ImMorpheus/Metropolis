package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.invitation.InvitationService;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Optional;

class InviteCommand extends AbstractCitizenCommand {

    InviteCommand() {
        super(
                MPGenericArguments.player(Text.of("players")),
                InputTokenizer.spaceSplitString(),
                Metropolis.ID + ".commands.town.invite",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Collection<Player> players = context.getAll("players");

        final InvitationService is = Sponge.getServiceManager().provideUnchecked(InvitationService.class);

        final Text invite = TextUtil.watermark(TextColors.AQUA, NameUtil.getDisplayName(source), " invited you to join ", t.getName());
        for (Player player : players) {
            is.create(source.getUniqueId(), player.getUniqueId(), t);
            player.sendMessage(invite);
        }

        return CommandResult.success();
    }

}
