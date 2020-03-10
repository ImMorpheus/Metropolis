package me.morpheus.metropolis.rank;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.health.IncidentService;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.config.ConfigUtil;
import me.morpheus.metropolis.configurate.serialize.Object2IntSerializer;
import me.morpheus.metropolis.error.MPGenericErrors;
import me.morpheus.metropolis.health.MPIncident;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
        if (Files.exists(RankLoader.RANK)) {
            final List<Rank> ranks = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(RankLoader.RANK)) {
                for (Path file : stream) {
                    MPLog.getLogger().info("Loading rank from {}", file.getFileName());
                    ranks.add(load(file));
                }
                return ranks;
            } catch (Exception e) {
                Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                        .create(new MPIncident(MPGenericErrors.config(), e));
                return Collections.emptyList();
            }
        }

        return Arrays.asList(
                new MPRank("citizen", "Citizen", false, true, true, getCitizenDefaultPermissions()),
                new MPRank("mayor", "Mayor", true, false, false, getMayorDefaultPermissions())
        );
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

        ObjectMapper<MPRank>.BoundInstance mapper = ObjectMapper.forClass(MPRank.class).bindToNew();
        CommentedConfigurationNode node = loader.load();
        mapper.populate(node);

        SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
        mapper.serialize(n);
        loader.save(n);
        MPRank rank = mapper.getInstance();
        rank.getPermissions().defaultReturnValue(Integer.MIN_VALUE);

        return rank;
    }

    @Override
    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            if (Files.notExists(RankLoader.RANK)) {
                try {
                    Files.createDirectories(RankLoader.RANK);
                } catch (IOException e) {
                    throw new CompletionException(e);
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

    private Object2IntMap<Flag> getCitizenDefaultPermissions() {
        Object2IntMap<Flag> map = new Object2IntOpenHashMap<>();
        for (Flag flag : Sponge.getRegistry().getAllOf(Flag.class)) {
            map.put(flag, 0);
        }
        return map;
    }

    private Object2IntMap<Flag> getMayorDefaultPermissions() {
        Object2IntMap<Flag> map = new Object2IntOpenHashMap<>();
        for (Flag flag : Sponge.getRegistry().getAllOf(Flag.class)) {
            map.put(flag, 127);
        }
        return map;
    }
}
