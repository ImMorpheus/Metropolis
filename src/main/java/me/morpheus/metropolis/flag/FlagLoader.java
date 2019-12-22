package me.morpheus.metropolis.flag;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.config.ConfigUtil;
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
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FlagLoader implements CustomResourceLoader<Flag> {

    private static final Path FLAG = ConfigUtil.CUSTOM.resolve("flag");

    @Override
    public String getId() {
        return "flag";
    }

    @Override
    public String getName() {
        return "Flag";
    }

    @Override
    public Collection<Flag> load() {
        Set<Flag> flags = new HashSet<>();

        if (Files.exists(FlagLoader.FLAG)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(FlagLoader.FLAG)) {
                for (Path file : stream) {
                    MPLog.getLogger().info("Loading flag from {}", file.getFileName());

                    try {
                        if (!flags.add(load(file))) {
                            MPLog.getLogger().warn("Duplicate flag from {}", file.toAbsolutePath());
                        }
                    } catch (IOException | ObjectMappingException e) {
                        MPLog.getLogger().error("Unable to load flag", e);
                    }
                }
            } catch (IOException e) {
                MPLog.getLogger().error("Unable to load flags", e);
                return Collections.emptyList();
            }
        }

        if (flags.isEmpty()) {
            flags.add(new MPFlag("block_break", "Block Break"));
            flags.add(new MPFlag("block_change", "Block Change"));
            flags.add(new MPFlag("block_place", "Block Place"));
            flags.add(new MPFlag("damage", "Damage"));
            flags.add(new MPFlag("interact_block", "Interact Block"));
            flags.add(new MPFlag("interact_entity", "Interact Entity"));
        }
        return flags;

    }

    @Override
    public Flag load(Path path) throws IOException, ObjectMappingException {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(Object2IntMap.class), new Object2IntSerializer());

        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(options)
                .setPath(path)
                .build();

        ObjectMapper.BoundInstance mapper = ObjectMapper.forClass(MPFlag.class).bindToNew();
        CommentedConfigurationNode node = loader.load();
        mapper.populate(node);

        SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
        mapper.serialize(n);
        loader.save(n);

        return (Flag) mapper.getInstance();
    }

    @Override
    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            if (Files.notExists(FlagLoader.FLAG)) {
                try {
                    Files.createDirectories(FlagLoader.FLAG);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            final Collection<? extends CatalogType> types = Sponge.getRegistry().getAllOf(Flag.class);
            for (CatalogType catalogType : types) {
                final Path save = FlagLoader.FLAG.resolve(catalogType.getId() + ".conf");
                try {
                    if (Files.notExists(save)) {
                        Files.createFile(save);
                    }
                    final SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
                    ObjectMapper.forObject(catalogType).serialize(n);
                    HoconConfigurationLoader.builder()
                            .setPath(save)
                            .build()
                            .save(n);
                } catch (ObjectMappingException | IOException e) {
                    MPLog.getLogger().error("Error while saving catalog {} {}", catalogType.getClass(), catalogType.getId());
                    MPLog.getLogger().error("Exception:", e);
                }
            }
        });
    }
}
