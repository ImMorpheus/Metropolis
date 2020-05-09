package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.teleport.TeleportTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class OutpostCommand extends AbstractCitizenCommand {

    OutpostCommand() {
        super(
                GenericArguments.optional(
                        GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false)
                ),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.outpost.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Optional<OutpostData> odOpt = t.get(OutpostData.class);

        if (!odOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Your town has no outpost"));
            return CommandResult.empty();
        }

        final Optional<Text> nameOpt = context.getOne("name");

        if (!nameOpt.isPresent()) {
            final List<Text> outposts = new ArrayList<>(odOpt.get().outposts().size());
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

        final Text name = nameOpt.get();
        final Location<World> out = odOpt.get().outposts().get().get(name.toPlain());

        if (out == null) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "There is no outpost named ", name));
            return CommandResult.empty();
        }

        final Optional<Entity> vehicleOpt = source.getVehicle();
        if (vehicleOpt.isPresent()) {
            boolean success = source.setVehicle(null);
            if (!success) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Dismount from your vehicle"));
                return CommandResult.empty();
            }
        }
        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
            frame.addContext(EventContextKeys.TELEPORT_TYPE, TeleportTypes.COMMAND);
            frame.addContext(EventContextKeys.PLUGIN, plugin);
            source.transferToWorld(out.getExtent(), out.getPosition());
        }
        return CommandResult.success();
    }
}
