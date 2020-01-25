package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.EconomyCategory;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.TownTypes;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class PriceCommand extends AbstractMPCommand {

    PriceCommand() {
        super(MPGenericArguments.townOrHomeTown(Text.of("town")), InputTokenizer.rawInput());
    }

    @Override
    protected CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        final ConfigService cs = Sponge.getServiceManager().provideUnchecked(ConfigService.class);

        if (!cs.getGlobal().getEconomyCategory().isEnabled()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Economy integration is not enabled"));
            return CommandResult.empty();
        }

        final EconomyCategory economy = cs.getGlobal().getEconomyCategory();
        final TownType type = context.<Town>getOne("town")
                .map(Town::getType)
                .orElse(TownTypes.SETTLEMENT);

        final Collection<PlotType> types = Sponge.getRegistry().getAllOf(PlotType.class);
        final List<Text> contents = new ArrayList<>(types.size() + 3);
        contents.add(Text.of(TextColors.DARK_GREEN, "Creation: ", TextColors.GREEN, economy.getTownCreationPrice()));
        contents.add(Text.of(TextColors.DARK_GREEN, "Spawn: ", TextColors.GREEN, type.getSpawnPrice()));
        contents.add(Text.of(TextColors.DARK_GREEN, "Tax: ", TextColors.GREEN, type.getTaxFunction()));
        for (PlotType t : types) {
            contents.add(Text.of(TextColors.DARK_GREEN, t.getName(), ": ", TextColors.GREEN, type.getMaxPlots(t)));
        }

        PaginationList.builder()
                .title(Text.of(TextColors.GOLD, "[", TextColors.YELLOW, "Prices", TextColors.GOLD, "]"))
                .contents(contents)
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
        return Optional.empty();
    }
}
