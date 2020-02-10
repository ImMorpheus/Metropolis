package me.morpheus.metropolis.commands.town.invitation;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.town.invitation.Invitation;
import me.morpheus.metropolis.api.town.invitation.InvitationService;
import me.morpheus.metropolis.util.NameUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

class ListCommand extends AbstractPlayerCommand {

    public ListCommand() {
        super(
                Metropolis.ID + ".commands.town.invitation.list",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context) {
        final InvitationService is = Sponge.getServiceManager().provideUnchecked(InvitationService.class);

        final Collection<Invitation> invitations = is.getAll(source.getUniqueId());

        final UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);

        final List<Text> messages = new ArrayList<>(invitations.size());
        for (Invitation invitation : invitations) {
            if (invitation.isValid()) {
                final Text accept = Text.builder("accept")
                        .onHover(TextActions.showText(Text.of(TextColors.LIGHT_PURPLE, "Click to accept")))
                        .onClick(TextActions.executeCallback(target -> invitation.accept()))
                        .build();

                final Text refuse = Text.builder("refuse")
                        .onHover(TextActions.showText(Text.of(TextColors.LIGHT_PURPLE, "Click to refuse")))
                        .onClick(TextActions.executeCallback(target -> invitation.refuse()))
                        .build();


                invitation.getTown().ifPresent(t -> {
                    final Text tName = t.getName();

                    Text message;
                    if (invitation.getSource().isPresent()) {
                        Text name = uss.get(invitation.getSource().get())
                                .map(NameUtil::getDisplayName)
                                .orElse(Text.of("Someone "));
                        message = Text.of(TextColors.AQUA, name, " invited you to join ", tName);
                    } else {
                        message = Text.of(TextColors.AQUA, "You've been invited to join ", tName);
                    }
                    messages.add(message.concat(Text.of("(", accept, " / ", refuse, ")")));
                });
            }
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Invitations", TextColors.GOLD, "]"))
                .contents(messages)
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }
}
