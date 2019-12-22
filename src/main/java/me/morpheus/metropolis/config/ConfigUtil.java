package me.morpheus.metropolis.config;

import me.morpheus.metropolis.Metropolis;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigRoot;
import org.spongepowered.api.plugin.PluginContainer;

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
    public static final Path CUSTOM;
    public static final Path CONF;

    private ConfigUtil() {}
}

