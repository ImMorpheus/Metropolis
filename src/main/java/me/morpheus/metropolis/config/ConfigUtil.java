package me.morpheus.metropolis.config;

import me.morpheus.metropolis.Metropolis;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigRoot;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigUtil {

    static {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        final ConfigRoot metropolis = Sponge.getConfigManager().getPluginConfig(plugin);

        DATA = metropolis.getDirectory().resolve("data");
        CUSTOM = metropolis.getDirectory().resolve("custom");
        CONF = metropolis.getConfigPath();
    }


    public static final Path DATA;
    public static final Path TOWN_DATA = DATA.resolve("town");
    public static final Path PLOT_DATA = DATA.resolve("plot");
    public static final Path CUSTOM;
    public static final Path RANK = CUSTOM.resolve("rank");
    public static final Path FLAG = CUSTOM.resolve("flag");
    public static final Path TOWN_TYPE = CUSTOM.resolve("town-type");
    public static final Path CONF;


    public static void init() throws IOException {
        Files.createDirectories(DATA);
        createDir(TOWN_DATA);
        createDir(PLOT_DATA);
        createDir(CUSTOM);
        createDir(RANK);
        createDir(FLAG);
        createDir(TOWN_TYPE);
    }

    private static void createDir(Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }
    }

    private ConfigUtil() {}
}

