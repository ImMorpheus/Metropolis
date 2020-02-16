package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

class OutpostCommand extends AbstractCitizenCommand {

    OutpostCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false)),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.outpost",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Text name = context.requireOne("name");

        final Optional<OutpostData> odOpt = t.get(OutpostData.class);

        if (!odOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Your town has no outpost"));
            return CommandResult.empty();
        }

        final Location<World> out = odOpt.get().outposts().get().get(name.toPlain());

        if (out == null) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "There is no outpost named ", name));
            return CommandResult.empty();
        }

        source.transferToWorld(out.getExtent(), out.getPosition());
        return CommandResult.success();
    }
}
