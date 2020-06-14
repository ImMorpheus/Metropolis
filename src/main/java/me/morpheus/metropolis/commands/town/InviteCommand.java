package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.invitation.Invitation;
import me.morpheus.metropolis.api.town.invitation.InvitationService;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InviteCommand extends AbstractCitizenCommand {

    public InviteCommand() {
        super(
                GenericArguments.allOf(MPGenericArguments.visiblePlayer(Text.of("players"))),
                InputTokenizer.spaceSplitString(),
                Metropolis.ID + ".commands.town.invite.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Collection<Player> players = context.getAll("players");

        final InvitationService is = Sponge.getServiceManager().provideUnchecked(InvitationService.class);

        final Text.Builder builder = Text.builder().append(TextUtil.watermark(TextColors.AQUA, NameUtil.getDisplayName(source), " invited you to join ", t.getName()));
        for (Player player : players) {
            final Invitation invitation = is.create(source.getUniqueId(), player.getUniqueId(), t);

            final Text accept = Text.builder("accept")
                    .onHover(TextActions.showText(Text.of(TextColors.LIGHT_PURPLE, "Click to accept")))
                    .onClick(TextActions.executeCallback(target -> {
                        if (invitation.accept()) {
                            invitation.getTown().ifPresent(town -> town.sendMessage(Text.of(NameUtil.getDisplayName(source), " joined the town")));
                        }
                    }))
                    .build();

            final Text refuse = Text.builder("refuse")
                    .onHover(TextActions.showText(Text.of(TextColors.LIGHT_PURPLE, "Click to refuse")))
                    .onClick(TextActions.executeCallback(target -> {
                        if (invitation.refuse()) {
                            invitation.getTown().ifPresent(town -> town.sendMessage(Text.of(NameUtil.getDisplayName(source), " refused to join the town")));
                        }

                    }))
                    .build();

            builder.append(Text.of(" (", accept, " / ", refuse, ")"));
            player.sendMessage(builder.build());
        }

        return CommandResult.success();
    }

}
