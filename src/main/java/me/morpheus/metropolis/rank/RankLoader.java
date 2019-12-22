package me.morpheus.metropolis.rank;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.rank.Rank;
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

public class RankLoader implements CustomResourceLoader<Rank> {

    private static final Path RANK = ConfigUtil.CUSTOM.resolve("rank");

    @Override
    public String getId() {
        return "rank";
    }

    @Override
    public String getName() {
        return "Rank";
    }

    @Override
    public Collection<Rank> load() {
        Set<Rank> ranks = new HashSet<>();

        if (Files.exists(RankLoader.RANK)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(RankLoader.RANK)) {
                for (Path file : stream) {
                    MPLog.getLogger().info("Loading rank from {}", file.getFileName());

                    try {
                        if (!ranks.add(load(file))) {
                            MPLog.getLogger().warn("Duplicate rank from {}", file.toAbsolutePath());
                        }
                    } catch (IOException | ObjectMappingException e) {
                        MPLog.getLogger().error("Unable to load rank", e);
                    }
                }
            } catch (IOException e) {
                MPLog.getLogger().error("Unable to load ranks", e);
                return Collections.emptyList();
            }
        }

        if (ranks.isEmpty()) {
            ranks.add(new MPRank("citizen", "Citizen", false, true, true, 0, Object2IntMaps.emptyMap()));
            ranks.add(new MPRank("mayor", "Mayor", true, false, false, 250, Object2IntMaps.emptyMap()));
        }
        return ranks;

    }

    @Override
    public Rank load(Path path) throws IOException, ObjectMappingException {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(Object2IntMap.class), new Object2IntSerializer());

        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(options)
                .setPath(path)
                .build();

        ObjectMapper.BoundInstance mapper = ObjectMapper.forClass(MPRank.class).bindToNew();
        CommentedConfigurationNode node = loader.load();
        mapper.populate(node);

        SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
        mapper.serialize(n);
        loader.save(n);

        return (Rank) mapper.getInstance();
    }

    @Override
    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            if (Files.notExists(RankLoader.RANK)) {
                try {
                    Files.createDirectories(RankLoader.RANK);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            final Collection<? extends CatalogType> types = Sponge.getRegistry().getAllOf(Rank.class);
            for (CatalogType catalogType : types) {
                final Path save = RankLoader.RANK.resolve(catalogType.getId() + ".conf");
                try {
                    if (Files.notExists(save)) {
                        Files.createFile(save);
                    }
                    SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
                    ObjectMapper<CatalogType>.BoundInstance mapper = ObjectMapper.forObject(catalogType);
                    mapper.serialize(n);
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
