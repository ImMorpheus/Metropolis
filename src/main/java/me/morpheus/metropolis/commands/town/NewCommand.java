package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.api.rank.Ranks;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.EconomyUtil;
import me.morpheus.metropolis.util.NameUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.math.BigDecimal;
import java.util.Optional;

public class NewCommand extends AbstractPlayerCommand {

    public NewCommand() {
        super(
                GenericArguments.text(Text.of("name"), TextSerializers.FORMATTING_CODE, false),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.new.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context) throws CommandException {
        final Optional<CitizenData> cdOpt = source.get(CitizenData.class);

        if (cdOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You have a town"));
            return CommandResult.empty();
        }

        final Text name = context.requireOne("name");

        final PlotService ps = Sponge.getServiceManager().provideUnchecked(PlotService.class);
        if (ps.get(source.getLocation()).isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This plot is claimed by another town"));
            return CommandResult.empty();
        }

        final GlobalConfig global = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        if (global.getEconomyCategory().isEnabled()) {
            final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
            final Optional<UniqueAccount> accOpt = es.getOrCreateAccount(source.getUniqueId());
            if (!accOpt.isPresent()) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to retrieve player account"));
                return CommandResult.empty();
            }

            try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
                frame.addContext(EventContextKeys.PLUGIN, plugin);
                final BigDecimal amount = BigDecimal.valueOf(global.getEconomyCategory().getTownCreationPrice());
                final ResultType result = accOpt.get().withdraw(es.getDefaultCurrency(), amount, frame.getCurrentCause()).getResult();
                if (result != ResultType.SUCCESS) {
                    final String error = EconomyUtil.getErrorMessage(result);
                    source.sendMessage(TextUtil.watermark(TextColors.RED, error));
                    return CommandResult.empty();
                }
            }
        }

        final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);

        if (ts.towns().anyMatch(t -> t.getName().toPlain().equals(name.toPlain()))) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, name.toPlain(), " already exists"));
            return CommandResult.empty();
        }
        final Optional<Town> tOpt = ts.create(name, source.getLocation());

        final boolean success = tOpt.isPresent() && tOpt.get().accept(source.getUniqueId(), Ranks.MAYOR);
        if (!success) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to create a town"));
            tOpt.ifPresent(Town::disband);
            return CommandResult.empty();
        }

        final Town t = tOpt.get();

        final boolean claimed = t.claim(source.getLocation(), PlotTypes.HOMEBLOCK);
        if (!claimed) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "Unable to claim homeblock"));
            t.disband();
            return CommandResult.empty();
        }

        final Text broadcast = TextUtil.watermark(TextColors.AQUA, NameUtil.getDisplayName(source), " created a new town called ", t.getName());
        Sponge.getServer().getBroadcastChannel().send(broadcast);

        return CommandResult.success();
    }
}
