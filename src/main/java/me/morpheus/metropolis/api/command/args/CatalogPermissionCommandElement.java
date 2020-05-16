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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class CatalogPermissionCommandElement<T extends CatalogType> extends CommandElement {

    private final Class<T> type;
    private final Function<T, String> hasPermission;

    CatalogPermissionCommandElement(Class<T> type, Function<T, String> hasPermission, @Nullable Text key) {
        super(key);
        this.type = type;
        this.hasPermission = hasPermission;
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (!args.hasNext()) {
            return null;
        }
        final String id = args.next();

        final T catalog = Sponge.getRegistry().getType(this.type, id)
                .orElseThrow(() -> args.createError(Text.of("Invalid ", this.type.getSimpleName(), "!")));
        final String perm = this.hasPermission.apply(catalog);
        if (!perm.isEmpty() && !source.hasPermission(perm)) {
            throw args.createError(Text.of("You do not have permission to use the ", catalog.getName(), " argument"));
        }
        return catalog;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        final String arg = args.nextIfPresent().orElse("");

        return Sponge.getRegistry().getAllOf(this.type).stream()
                .filter(f -> f.getId().startsWith(arg))
                .filter(f -> {
                    final String perm = this.hasPermission.apply(f);
                    return !perm.isEmpty() && src.hasPermission(perm);
                })
                .map(CatalogType::getId)
                .collect(Collectors.toList());
    }
}

