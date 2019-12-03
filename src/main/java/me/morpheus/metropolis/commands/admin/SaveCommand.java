package me.morpheus.metropolis.commands.admin;

import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.util.TextUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

class SaveCommand extends AbstractMPCommand {

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        Sponge.getServiceManager().provideUnchecked(TownService.class)
                .saveAll()
                .thenRun(() -> source.sendMessage(TextUtil.watermark("Towns saved")));

        Sponge.getServiceManager().provideUnchecked(PlotService.class)
                .saveAll()
                .thenRun(() -> source.sendMessage(TextUtil.watermark("Plots saved")));

        return CommandResult.success();
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("save"));
    }
}
