package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

public final class MPGenericArguments {

    private MPGenericArguments() {}

    public static CommandElement player(Text key) {
        return new VisiblePlayerCommandElement(key);
    }

    public static CommandElement citizen(Text key) {
        return new CitizenCommandElement(key);
    }

    public static CommandElement town(Text key) {
        return new TownCommandElement(key);
    }

    public static CommandElement townOrHomeTown(Text key) {
        return new TownCommandElement(key, true);
    }

    public static <T extends CatalogType> CommandElement catalog(Class<T> type, Text key) {
        return new CatalogCommandElement<>(type, key);
    }

    public static CommandElement empty() {
        return new EmptyCommandElement();
    }

}
