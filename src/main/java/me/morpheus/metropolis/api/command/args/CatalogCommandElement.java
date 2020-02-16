package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

class CatalogCommandElement<T extends CatalogType> extends CommandElement {

    private final Class<T> type;

    CatalogCommandElement(Class<T> type, @Nullable Text key) {
        super(key);
        this.type = type;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (!args.hasNext()) {
            return null;
        }

        final String id = args.next();

        return Sponge.getRegistry().getType(this.type, id)
                .orElseThrow(() -> args.createError(Text.of("Invalid ", this.type.getSimpleName(), "!")));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        final String arg = args.nextIfPresent().orElse("");

        return Sponge.getRegistry().getAllOf(this.type).stream()
                .filter(f -> f.getId().startsWith(arg))
                .map(CatalogType::getId)
                .collect(Collectors.toList());
    }
}
