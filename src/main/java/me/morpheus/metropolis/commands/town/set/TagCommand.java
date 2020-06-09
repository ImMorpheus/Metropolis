package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
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

public class TagCommand extends AbstractCitizenCommand {

    public TagCommand() {
        super(
                GenericArguments.text(Text.of("tag"), TextSerializers.FORMATTING_CODE, false),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.set.tag.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final Text tag = context.requireOne("tag");
        final String plain = tag.toPlain();

        final TownCategory townCategory = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal().getTownCategory();

        final byte tagMaxLength = townCategory.getTagMaxLength();
        if (plain.length() > tagMaxLength) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Tag can't be longer than ", tagMaxLength, " char"));
            return CommandResult.empty();
        }

        final byte tagMinLength = townCategory.getTagMinLength();
        if (plain.length() < tagMinLength) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Tag can't be shorter than ", tagMinLength, " char"));
            return CommandResult.empty();
        }

        t.setTag(tag);
        t.sendMessage(Text.of("Town tag set to ", tag));

        return CommandResult.success();
    }
}
