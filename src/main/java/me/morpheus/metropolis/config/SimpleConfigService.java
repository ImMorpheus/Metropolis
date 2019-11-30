package me.morpheus.metropolis.config;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.configurate.serialize.Object2IntSerializer;
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

public final class SimpleConfigService implements ConfigService {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final ObjectMapper.BoundInstance mapper;

    public SimpleConfigService() {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(Object2IntMap.class), new Object2IntSerializer());

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
    public void reload() throws ObjectMappingException, IOException {
        CommentedConfigurationNode node = this.loader.load();
        this.mapper.populate(node);
    }

    @Override
    public void save() throws ObjectMappingException, IOException {
        SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
        this.mapper.serialize(n);
        this.loader.save(n);
    }
}
