package me.morpheus.metropolis.commands.town;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.command.AbstractPlayerCommand;
import me.morpheus.metropolis.api.town.visibility.Visibilities;
import me.morpheus.metropolis.util.EconomyUtil;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.teleport.TeleportTypes;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.Optional;

class SpawnCommand extends AbstractPlayerCommand {

    SpawnCommand() {
        super(
                MPGenericArguments.townOrHomeTown(Text.of("townOrHomeTown")),
                MinimalInputTokenizer.INSTANCE,
                Metropolis.ID + ".commands.town.spawn",
                Text.of()
        );
    }

    @Override
    public CommandResult process(Player source, CommandContext context) throws CommandException {
        final Optional<Town> townOpt = context.getOne("townOrHomeTown");

        if (!townOpt.isPresent()) {
            source.sendMessage(TextUtil.watermark(TextColors.RED, "You don't have a town"));
            return CommandResult.empty();
        }

        final Optional<CitizenData> cdOpt = source.get(CitizenData.class);

        if (townOpt.get().getVisibility() != Visibilities.PUBLIC && (!cdOpt.isPresent() || cdOpt.get().town().get().intValue() != townOpt.get().getId())) {
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
            final ResultType result = EconomyUtil.withdraw(accOpt.get(), es.getDefaultCurrency(), BigDecimal.valueOf(townOpt.get().getType().getSpawnPrice()));
            if (result == ResultType.ACCOUNT_NO_FUNDS) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Not enough money"));
                return CommandResult.empty();
            }
            if (result != ResultType.SUCCESS) {
                source.sendMessage(TextUtil.watermark(TextColors.RED, "Error while paying: ", result.name()));
                return CommandResult.empty();
            }
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
            source.transferToWorld(townOpt.get().getSpawn().getExtent(), townOpt.get().getSpawn().getPosition());
        }
        return CommandResult.success();
    }
}
