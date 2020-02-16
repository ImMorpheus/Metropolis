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

import java.util.Optional;

class TagCommand extends AbstractCitizenCommand {

    TagCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.string(Text.of("tag"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.set.tag",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final String tag = context.requireOne("tag");

        final TownCategory townCategory = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal().getTownCategory();

        final int tagMaxLength = townCategory.getTagMaxLength();
        if (tag.length() > tagMaxLength) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Tag can't be longer than ", tagMaxLength, " char"));
            return CommandResult.empty();
        }

        final int tagMinLength = townCategory.getTagMinLength();
        if (tag.length() < tagMinLength) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Tag can't be shorter than ", tagMinLength, " char"));
            return CommandResult.empty();
        }

        t.setTag(Text.of(tag));
        t.sendMessage(TextUtil.watermark(TextColors.AQUA, "Town tag set to ", tag));

        return CommandResult.success();
    }
}
