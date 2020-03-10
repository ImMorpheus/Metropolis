package me.morpheus.metropolis.config;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.config.category.SimplePlotCategory;
import me.morpheus.metropolis.configurate.serialize.Object2IntSerializer;
import me.morpheus.metropolis.configurate.serialize.Reference2ByteSerializer;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class SimpleConfigService implements ConfigService {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final ObjectMapper.BoundInstance mapper;

    public SimpleConfigService() {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(Reference2ByteMap.class), new Reference2ByteSerializer());

        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);

        this.loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(options)
                .setPath(ConfigUtil.CONF)
                .build();

        try {
            this.mapper = ObjectMapper.forClass(Global.class).bindToNew();
        } catch (ObjectMappingException e) {
            MPLog.getLogger().error("Failed to populate configuration");
            throw new RuntimeException(e);
        }
    }

    @Override
    public GlobalConfig getGlobal() {
        return (Global) this.mapper.getInstance();
    }

    @Override
    public CompletableFuture<Void> reload() {
        if (Files.notExists(ConfigUtil.CONF)) {
            save();
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            try {
                CommentedConfigurationNode node = this.loader.load();
                this.mapper.populate(node);
                ((SimplePlotCategory) getGlobal().getTownCategory().getPlotCategory()).getUnowned().defaultReturnValue(Byte.MIN_VALUE);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public void populate() throws ObjectMappingException, IOException {
        if (Files.notExists(ConfigUtil.CONF)) {
            return;
        }
        CommentedConfigurationNode node = this.loader.load();
        this.mapper.populate(node);
        ((SimplePlotCategory) getGlobal().getTownCategory().getPlotCategory()).getUnowned().defaultReturnValue(Byte.MIN_VALUE);
    }

    @Override
    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
            try {
                if (Files.notExists(ConfigUtil.CONF)) {
                    Files.createFile(ConfigUtil.CONF);
                }
                this.mapper.serialize(n);
                this.loader.save(n);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}
