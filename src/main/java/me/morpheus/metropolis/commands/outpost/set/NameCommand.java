package me.morpheus.metropolis.commands.outpost.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.AbstractHomeTownCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.TownCategory;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.TownKeys;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.value.mutable.CompositeValueStore;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class NameCommand extends AbstractCitizenCommand {

    public NameCommand() {
        super(
                GenericArguments.seq(
                        GenericArguments.withSuggestions(
                                GenericArguments.string(Text.of("outpost")),
                                (CommandSource source) -> {
                                    if (!(source instanceof DataHolder)) {
                                        return Collections.emptyList();
                                    }
                                    final Optional<CitizenData> cdOpt = ((DataHolder) source).get(CitizenData.class);

                                    if (!cdOpt.isPresent()) {
                                        return Collections.emptyList();
                                    }

                                    final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
                                    final Optional<Town> townOpt = ts.get(cdOpt.get().town().get().intValue());
                                    if (!townOpt.isPresent()) {
                                        return Collections.emptyList();
                                    }

                                    final Optional<OutpostData> outpostOpt = townOpt.get().get(OutpostData.class);
                                    if (!outpostOpt.isPresent()) {
                                        return Collections.emptyList();
                                    }

                                    return outpostOpt.get().outposts().keySet();
                                }
                        ),
                        GenericArguments.string(Text.of("name"))
                ),
                InputTokenizer.quotedStrings(false),
                Metropolis.ID + ".commands.outpost.set.name.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final String outpost = context.requireOne("outpost");
        final String name = context.requireOne("name");
        final Optional<OutpostData> outpostOpt = t.get(OutpostData.class);
        if (!outpostOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "No outpost data"));
            return CommandResult.empty();
        }

        final Map<String, Location<World>> outposts = outpostOpt.get().get(TownKeys.OUTPOSTS).get();
        if (outposts.containsKey(name)) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "An outpost named ", name, " already exists"));
            return CommandResult.empty();
        }

        final Location<World> pos = outposts.remove(outpost);
        outposts.put(name, pos);
        t.sendMessage(Text.of("Outpost ", outpost, " was renamed to ", name));

        return CommandResult.success();
    }
}
