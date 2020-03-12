package me.morpheus.metropolis.commands.town.set;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractCitizenCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.town.TownKeys;
import me.morpheus.metropolis.api.data.town.economy.TaxData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class TaxCommand extends AbstractCitizenCommand {

    TaxCommand() {
        super(
                GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("tax"))),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.set.tax",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context, CitizenData cd, Town t) throws CommandException {
        final double tax = context.requireOne("tax");

        if (tax == 0) {
            t.remove(TaxData.class);
        } else {

            final Optional<TaxData> tdOpt = t.get(TaxData.class);

            if (!tdOpt.isPresent()) {
                final TaxData td = Sponge.getDataManager().getManipulatorBuilder(TaxData.class).get().create();
                td.set(TownKeys.TAX, tax);
                t.offer(td);
            } else {
                tdOpt.get().set(TownKeys.TAX, tax);
            }
        }
        t.sendMessage(TextUtil.watermark(TextColors.AQUA, "Town tax set to ", tax));

        return CommandResult.success();
    }
}
