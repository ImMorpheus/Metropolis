package me.morpheus.metropolis.commands.admin;

import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.AbstractMPCommand;
import me.morpheus.metropolis.task.DailyTask;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

class ForceTaxCommand extends AbstractMPCommand {

    public ForceTaxCommand() {
        super(
                Metropolis.ID + ".commands.admin.forcetax",
                Text.of()
        );
    }

    @Override
    public CommandResult process(CommandSource source, CommandContext context) throws CommandException {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();

        Sponge.getScheduler().createTaskBuilder()
                .name(Metropolis.ID + "+daily")
                .execute(DailyTask::run)
                .submit(plugin);

        return CommandResult.success();
    }
}

