package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.List;

class OptionalSequenceCommandElement extends CommandElement {

    private final CommandElement seq;

    OptionalSequenceCommandElement(CommandElement... elements) {
        super(null);
        this.seq = GenericArguments.seq(elements);
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        if (!args.hasNext()) {
            return;
        }
        this.seq.parse(source, args, context);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        return null;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return this.seq.complete(src, args, context);
    }

    @Override
    public Text getUsage(CommandSource commander) {
        final Text.Builder builder = Text.builder();
        builder.append(Text.of('['));
        builder.append(this.seq.getUsage(commander));
        builder.append(Text.of(']'));
        return builder.build();
    }
}
