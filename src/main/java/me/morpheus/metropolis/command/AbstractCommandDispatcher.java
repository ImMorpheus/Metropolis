package me.morpheus.metropolis.command;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.command.CommandDispatcher;
import me.morpheus.metropolis.command.mapping.ImmutableAliaslessCommandMapping;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandMessageFormatting;
import org.spongepowered.api.command.CommandNotFoundException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.ImmutableCommandMapping;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractCommandDispatcher implements CommandDispatcher {

    private final Map<String, CommandMapping> commands = new HashMap<>();

    @Override
    public Set<? extends CommandMapping> getCommands() {
        return ImmutableSet.copyOf(this.commands.values());
    }

    @Override
    public Set<String> getPrimaryAliases() {
        return this.commands.values().stream()
                .map(CommandMapping::getPrimaryAlias)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAliases() {
        return Collections.unmodifiableSet(this.commands.keySet());
    }

    @Override
    public Optional<? extends CommandMapping> get(String alias) {
        return Optional.ofNullable(this.commands.get(alias));
    }

    @Override
    public Optional<? extends CommandMapping> get(String alias, @Nullable CommandSource source) {
        final CommandMapping cm = this.commands.get(alias);
        if (cm == null) {
            return Optional.empty();
        }
        if (source != null && !cm.getCallable().testPermission(source)) {
            return Optional.empty();
        }
        return Optional.of(cm);
    }

    @Override
    public Set<? extends CommandMapping> getAll(String alias) {
        final CommandMapping cm = this.commands.get(alias);
        if (cm == null) {
            return Collections.emptySet();
        }
        return Collections.singleton(cm);
    }

    @Override
    public Multimap<String, CommandMapping> getAll() {
        return ImmutableMultimap.copyOf(this.commands.entrySet());
    }

    @Override
    public boolean containsAlias(String alias) {
        return this.commands.containsKey(alias);
    }

    @Override
    public boolean containsMapping(CommandMapping mapping) {
        return this.commands.containsValue(mapping);
    }

    @Override
    public Optional<CommandMapping> register(CommandCallable callable, String alias) {
        final CommandMapping mapping = new ImmutableAliaslessCommandMapping(callable, alias);

        this.commands.put(alias, mapping);

        return Optional.of(mapping);
    }

    @Override
    public Optional<CommandMapping> register(CommandCallable callable, String... aliases) {
        if (aliases.length == 0) {
            return Optional.empty();
        }

        final String primary = aliases[0];

        if (aliases.length == 1) {
            return register(callable, primary);
        }

        final List<String> secondary = Arrays.asList(aliases).subList(1, aliases.length);
        final CommandMapping mapping = new ImmutableCommandMapping(callable, primary, secondary);

        for (String alias : aliases) {
            this.commands.put(alias, mapping);
        }

        return Optional.of(mapping);
    }

    protected void registerManager(CommandDispatcher manager, String alias) {
        register(manager, alias);
        manager.registerDefaults();
    }

    @Override
    public boolean remove(CommandMapping mapping) {
        return this.commands.keySet().removeAll(mapping.getAllAliases());
    }

    @Override
    public Optional<CommandMapping> remove(String alias) {
        return Optional.ofNullable(this.commands.remove(alias));
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        final int index = arguments.indexOf(' ');

        final String root = index == -1 ? arguments : arguments.substring(0, index);

        final CommandMapping mapping = get(root, source)
                .orElseThrow(() -> new CommandNotFoundException(Text.of("Unknown command. Try /help for a list of commands"), root)); //TODO

        final String args = arguments.substring(root.length()).trim();
        final CommandCallable spec = mapping.getCallable();
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.pushCause(plugin);
            frame.pushCause(spec);

            try {
                return spec.process(source, args);
            } catch (ArgumentParseException.WithUsage ex) {
                throw new ArgumentParseException.WithUsage(ex, Text.of(mapping.getPrimaryAlias(), " ", ex.getUsage()));
            } catch (ArgumentParseException ex) {
                throw new ArgumentParseException.WithUsage(ex, Text.of(mapping.getPrimaryAlias(), " ", spec.getUsage(source)));
            }
        }
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        if (arguments.isEmpty()) {
            return this.commands.values().stream()
                    .filter(value -> value.getCallable().testPermission(source))
                    .map(CommandMapping::getPrimaryAlias)
                    .distinct()
                    .collect(Collectors.toList());
        }

        final int index = arguments.indexOf(' ');

        if (index == -1) {
            return this.commands.entrySet().stream()
                    .filter(e -> e.getValue().getCallable().testPermission(source))
                    .filter(e -> e.getKey().startsWith(arguments))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        final String root = arguments.substring(0, index);

        final Optional<? extends CommandMapping> cmOpt = get(root, source);
        if (!cmOpt.isPresent()) {
            return Collections.emptyList();
        }

        return cmOpt.get().getCallable().getSuggestions(source, arguments.substring(index + 1), targetPosition);
    }

    @Override
    public boolean testPermission(CommandSource source) {
        for (CommandMapping mapping : this.commands.values()) {
            if (mapping.getCallable().testPermission(source)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        if (this.commands.isEmpty()) {
            return Optional.empty();
        }
        final Text.Builder builder = Text.builder()
                .append(getShortDescription(source).orElse(Text.EMPTY))
                .append(Text.NEW_LINE)
                .append(Text.of("commands:"))
                .append(Text.NEW_LINE);

        this.commands.values().stream()
                .distinct()
                .filter(cm -> cm.getCallable().testPermission(source))
                .forEach(cm -> {
                    final Optional<Text> description = cm.getCallable().getShortDescription(source);
                    builder.append(Text.builder(cm.getPrimaryAlias())
                                    .color(TextColors.GREEN)
                                    .onClick(TextActions.suggestCommand(cm.getPrimaryAlias()))
                                    .build(),
                            CommandMessageFormatting.SPACE_TEXT, description.orElse(cm.getCallable().getUsage(source)));
                    builder.append(Text.NEW_LINE);
                });
        return Optional.of(builder.build());
    }

    @Override
    public Text getUsage(CommandSource source) {
        final Text.Builder builder = Text.builder();

        for (Map.Entry<String, CommandMapping> entry : this.commands.entrySet()) {
            if (entry.getValue().getCallable().testPermission(source)) {
                builder.append(Text.of(entry.getKey()));
                builder.append(CommandMessageFormatting.PIPE_TEXT);
            }

        }
        return builder.build();
    }
}
