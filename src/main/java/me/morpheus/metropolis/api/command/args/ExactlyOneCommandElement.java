package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;

class ExactlyOneCommandElement extends CommandElement {

    private final CommandElement element;

    ExactlyOneCommandElement(CommandElement element) {
        super(element.getKey());
        this.element = element;
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
        this.element.parse(source, args, context);
        if (context.getAll(this.element.getUntranslatedKey()).size() != 1) {
            final Text key = this.element.getKey();
            throw args.createError(Text.of("Argument ", key, " may have only one value!"));
        }
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        throw args.createError(Text.of("How ?"));
    }

    @Override
    public Text getUsage(CommandSource src) {
        return this.element.getUsage(src);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return this.element.complete(src, args, context);
    }
}
