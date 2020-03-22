package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.TownData;
import me.morpheus.metropolis.api.data.town.TownKeys;
import me.morpheus.metropolis.api.town.Town;
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
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

class MotdCommand extends AbstractCitizenCommand {

    MotdCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.text(Text.of("motd"), TextSerializers.FORMATTING_CODE, false)),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.set.motd",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Text motd = context.requireOne("motd");

        final Optional<TownData> tdOpt = t.get(TownData.class);

        if (!tdOpt.isPresent()) {
            final TownData td = Sponge.getDataManager().getManipulatorBuilder(TownData.class).get().create();
            td.set(TownKeys.MOTD, Optional.of(motd));
            t.offer(td);
        } else {
            tdOpt.get().set(TownKeys.MOTD, Optional.of(motd));
        }
        t.sendMessage(Text.of("Town motd set to ", motd));

        return CommandResult.success();
    }
}
