package me.morpheus.metropolis.api.command;

import me.morpheus.metropolis.api.command.args.MPGenericArguments;
import me.morpheus.metropolis.api.command.args.parsing.MinimalInputTokenizer;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractMPCommand implements CommandCallable {

    private final CommandElement args;
    private final InputTokenizer tokenizer;
    private final String permission;
    private final Text description;

    protected AbstractMPCommand(CommandElement args, InputTokenizer tokenizer, String permission, Text description) {
        this.args = args;
        this.tokenizer = tokenizer;
        this.permission = permission;
        this.description = description;
    }

    protected AbstractMPCommand(String permission, Text description) {
        this(MPGenericArguments.empty(), MinimalInputTokenizer.INSTANCE, permission, description);
    }

    @Override
    public final CommandResult process(CommandSource source, String arguments) throws CommandException {
        final CommandArgs commandArgs = new CommandArgs(arguments, this.tokenizer.tokenize(arguments, false));
        final CommandContext context = new CommandContext();

        this.args.parse(source, commandArgs, context);

        return process(source, context);
    }

    protected abstract CommandResult process(CommandSource source, CommandContext context) throws CommandException;

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        final CommandArgs args = new CommandArgs(arguments, this.tokenizer.tokenize(arguments, true));
        final CommandContext ctx = new CommandContext();
        if (targetPosition != null) {
            ctx.putArg(CommandContext.TARGET_BLOCK_ARG, targetPosition);
        }
        ctx.putArg(CommandContext.TAB_COMPLETION, true);
        final List<String> ret = this.args.complete(source, args, ctx);
        return Collections.unmodifiableList(ret);
    }

    @Override
    public boolean testPermission(CommandSource source) {
        if (source instanceof DataHolder) {
            Optional<CitizenData> cdOpt = ((DataHolder) source).get(CitizenData.class);
            if (cdOpt.isPresent()) {
                final Set<Context> contexts = new HashSet<>(source.getActiveContexts());
                contexts.add(new Context("rank", cdOpt.get().rank().get().getId()));
                return source.hasPermission(contexts, this.permission);
            }
        }
        return source.hasPermission(this.permission);
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(this.description);
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        final Text.Builder builder = Text.builder();
        getShortDescription(source).ifPresent((a) -> builder.append(a, Text.NEW_LINE));
        builder.append(getUsage(source));
        return Optional.of(builder.build());
    }

    @Override
    public Text getUsage(CommandSource source) {
        return this.args.getUsage(source);
    }
}
