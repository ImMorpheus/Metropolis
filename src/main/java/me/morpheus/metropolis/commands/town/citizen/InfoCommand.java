package me.morpheus.metropolis.commands.town.citizen;

import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.NameUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class InfoCommand extends AbstractMPCommand {

    InfoCommand() {
        super(GenericArguments.onlyOne(MPGenericArguments.citizen(Text.of("citizen"))), InputTokenizer.rawInput());
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        final User user = context.requireOne("citizen");
        final CitizenData cd = user.get(CitizenData.class).get();

        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        final Town t = ts.get(cd.town().get().intValue()).get();

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, NameUtil.getDisplayName(user), TextColors.GOLD, "]"))
                .contents(
                        Text.of(TextColors.DARK_GREEN, "Town: ", TextColors.GREEN, t.getName()),
                        Text.of(TextColors.DARK_GREEN, "Rank: ", TextColors.GREEN, cd.rank().get().getName()),
                        Text.of(TextColors.DARK_GREEN, "Friends: ", TextColors.GREEN, cd.friends().get()),
                        Text.of(TextColors.DARK_GREEN, "Joined: ", TextColors.GREEN, cd.joined().get())
                )
                .padding(Text.of(TextColors.GOLD, "-"))
                .sendTo(source);

        return CommandResult.success();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Short desc"));
    }
}
