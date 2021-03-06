package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.function.Function;

public final class MPGenericArguments {

    private MPGenericArguments() {}

    public static CommandElement visiblePlayer(Text key) {
        return new VisiblePlayerCommandElement(key);
    }

    public static CommandElement citizen(Text key) {
        return new CitizenCommandElement(key);
    }

    public static CommandElement town(Text key) {
        return new TownCommandElement(key);
    }

    public static CommandElement positiveBigDecimal(Text key) {
        return new PositiveBigDecimal(key);
    }

    public static <T extends CatalogType> CommandElement guardedCatalog(Class<T> type, Function<T, String> hasPermission, Text key) {
        return new CatalogPermissionCommandElement<>(type, hasPermission, key);
    }

    public static CommandElement exactlyOne(CommandElement element) {
        return new ExactlyOneCommandElement(element);
    }

}
