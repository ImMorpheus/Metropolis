package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.town.visibility.Visibilities;
import me.morpheus.metropolis.util.EconomyUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.teleport.TeleportTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Optional;

public class SpawnCommand extends AbstractPlayerCommand {

    public SpawnCommand() {
        super(
                GenericArguments.optional(
                        GenericArguments.requiringPermission(
                                MPGenericArguments.town(Text.of("town")),
                                "metropolis.commands.town.spawn.other"
                        )
                ),
                MinimalInputTokenizer.INSTANCE,
                "metropolis.commands.town.spawn.base",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context) throws CommandException {
        @Nullable final Town t = context.<Town>getOne("town")
                .orElseGet(() -> {
                    final Optional<CitizenData> cdOpt = source.get(CitizenData.class);
                    if (!cdOpt.isPresent()) {
                        return null;
                    }

                    final TownService ts = Sponge.getServiceManager().provideUnchecked(TownService.class);
                    return ts.get(cdOpt.get().town().get().intValue())
                            .orElseThrow(() -> new RuntimeException("Corrupted CitizenData (invalid town)"));
                });

        if (t == null) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        final Optional<CitizenData> cdOpt = source.get(CitizenData.class);

        if (t.getVisibility() != Visibilities.PUBLIC && (!cdOpt.isPresent() || cdOpt.get().town().get().intValue() != t.getId())) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "This town is not public"));
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
                final BigDecimal amount = BigDecimal.valueOf(t.getType().getSpawnPrice());
                final ResultType result = accOpt.get().withdraw(es.getDefaultCurrency(), amount, frame.getCurrentCause()).getResult();
                if (result != ResultType.SUCCESS) {
                    final String error = EconomyUtil.getErrorMessage(result);
                    source.sendMessage(TextUtil.watermark(TextColors.RED, error));
                    return CommandResult.empty();
                }
            }
        }
        final Optional<Entity> vehicleOpt = source.getVehicle();
        if (vehicleOpt.isPresent()) {
            final boolean success = source.setVehicle(null);
            if (!success) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Dismount from your vehicle"));
                return CommandResult.empty();
            }
        }
        try (final CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
            frame.addContext(EventContextKeys.TELEPORT_TYPE, TeleportTypes.COMMAND);
            frame.addContext(EventContextKeys.PLUGIN, plugin);
            final boolean success = source.transferToWorld(t.getSpawn().getExtent(), t.getSpawn().getPosition());
            if (!success) {
                return CommandResult.empty();
            }
        }
        source.sendMessage(TextUtil.watermark(TextColors.AQUA, "Teleported to ", t.getName(), " spawn"));
        return CommandResult.success();
    }
}
