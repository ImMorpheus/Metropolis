package me.morpheus.metropolis.api.command.args;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.morpheus.metropolis.util.Hacks;
import org.spongepowered.api.command.CommandMessageFormatting;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Set;

class OrderedSequenceCommandElement extends CommandElement {

    private final CommandElement[] elements;

    OrderedSequenceCommandElement(CommandElement... elements) {
        super(null);
        this.elements = elements;
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        for (CommandElement element : this.elements) {
            element.parse(source, args, context);
        }
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        return null;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        if (!args.hasNext()) {
            return this.elements[0].complete(src, args, context);
        }
        Set<String> completions = Sets.newHashSet();
        for (int i = 0, commandElementsLength = this.elements.length; i < commandElementsLength; i++) {
            CommandArgs.Snapshot state = args.getSnapshot();
            CommandElement element = this.elements[i];
            Class<? extends CommandElement> current = element.getClass();
            if (current == ExactlyOneCommandElement.class || current == Hacks.ONLY_ONE) {
                if (i < args.size() - 1) {
                    args.nextIfPresent();
                    continue;
                }
            }
            CommandContext.Snapshot contextSnapshot = context.createSnapshot();
            try {
                element.parse(src, args, context);

                CommandContext.Snapshot afterSnapshot = context.createSnapshot();
                if (state.equals(args.getSnapshot())) {
                    context.applySnapshot(contextSnapshot);
                    completions.addAll(element.complete(src, args, context));
                    args.applySnapshot(state);
                    context.applySnapshot(afterSnapshot);
                } else if (args.hasNext()) {
                    completions.clear();
                } else {
                    // What we might also have - we have no args left to parse so
                    // while the parse itself was successful, there could be other
                    // valid entries to add...
                    context.applySnapshot(contextSnapshot);
                    args.applySnapshot(state);
                    completions.addAll(element.complete(src, args, context));
                    if (element.getClass() != Hacks.OPTIONAL) {
                        break;
                    }

                    // The last element was optional, so we go back to before this
                    // element would have been parsed, and assume it never existed...
                    context.applySnapshot(contextSnapshot);
                    args.applySnapshot(state);
                }
            } catch (ArgumentParseException ignored) {
                args.applySnapshot(state);
                context.applySnapshot(contextSnapshot);
                completions.addAll(element.complete(src, args, context));
                break;
            }
        }
        return Lists.newArrayList(completions);
    }

    @Override
    public Text getUsage(CommandSource commander) {
        final Text.Builder build = Text.builder();
        for (int i = 0, commandElementsLength = this.elements.length; i < commandElementsLength; i++) {
            final CommandElement element = this.elements[i];
            final Text usage = element.getUsage(commander);
            if (!usage.isEmpty()) {
                build.append(usage);
                if (i == commandElementsLength - 1) {
                    build.append(CommandMessageFormatting.SPACE_TEXT);
                }
            }
        }
        return build.build();
    }
}
