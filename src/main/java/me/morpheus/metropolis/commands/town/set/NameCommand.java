package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.TownCategory;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
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

class NameCommand extends AbstractCitizenCommand {

    NameCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false)),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.set.name",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Text name = context.requireOne("name");

        final TownCategory townCategory = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal().getTownCategory();

        final int nameMaxLength = townCategory.getNameMaxLength();
        if (name.toPlain().length() > nameMaxLength) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Name can't be longer than ", nameMaxLength, " char"));
            return CommandResult.empty();
        }

        final int nameMinLength = townCategory.getNameMinLength();
        if (name.toPlain().length() < nameMinLength) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Name can't be shorter than ", nameMinLength, " char"));
            return CommandResult.empty();
        }

        t.setName(name);
        t.sendMessage(TextUtil.watermark(TextColors.AQUA, "your town was renamed to ", name));

        return CommandResult.success();
    }
}
