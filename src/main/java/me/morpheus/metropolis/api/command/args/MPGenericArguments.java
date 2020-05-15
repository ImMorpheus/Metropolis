package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.args.CommandElement;
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

    public static <T extends CatalogType> CommandElement catalog(Class<T> type, Text key) {
        return new CatalogCommandElement<>(type, key);
    }

    public static <T extends CatalogType> CommandElement guardedCatalog(Class<T> type, Function<T, String> hasPermission, Text key) {
        return new CatalogPermissionCommandElement<>(type, hasPermission, key);
    }

    public static CommandElement orderedSeq(CommandElement... elements) {
        return new OrderedSequenceCommandElement(elements);
    }

    public static CommandElement optionalSeq(CommandElement... elements) {
        return new OptionalSequenceCommandElement(elements);
    }

    public static CommandElement exactlyOne(CommandElement element) {
        return new ExactlyOneCommandElement(element);
    }

    public static CommandElement empty() {
        return new EmptyCommandElement();
    }

}
