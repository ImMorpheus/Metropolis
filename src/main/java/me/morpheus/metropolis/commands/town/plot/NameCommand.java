package me.morpheus.metropolis.commands.town.plot;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
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

class NameCommand extends AbstractHomeTownCommand {

    NameCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false)),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.plot.name",
                Text.of()
        );
    }

    @Override
    protected CommandResult process(Player source, CommandContext context, CitizenData cd, Town t, Plot plot) throws CommandException {
        final Text name = context.requireOne("name");

        plot.setName(name);
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Plot renamed to ", name));

        return CommandResult.success();
    }
}
