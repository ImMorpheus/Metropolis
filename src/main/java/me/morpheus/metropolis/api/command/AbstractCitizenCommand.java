package me.morpheus.metropolis.api.command;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public abstract class AbstractCitizenCommand extends AbstractPlayerCommand {

    protected AbstractCitizenCommand(CommandElement args, InputTokenizer tokenizer, String permission, Text description) {
        super(args, tokenizer, permission, description);
    }

    protected AbstractCitizenCommand(String permission, Text description) {
        super(permission, description);
    }

    @Override
    public final CommandResult process(Player source, CommandContext context) throws CommandException {
        final Optional<CitizenData> cdOpt = source.get(CitizenData.class);

        if (!cdOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
        final Town t = ts.get(cdOpt.get().town().get().intValue())
                .orElseThrow(() -> new RuntimeException("Corrupted CitizenData (invalid town)"));

        return process(source, context, cdOpt.get(), t);
    }

    protected abstract CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException;

}
