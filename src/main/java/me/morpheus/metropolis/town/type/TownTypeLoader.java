package me.morpheus.metropolis.town.type;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.data.plot.PlotKeys;
import me.morpheus.metropolis.api.health.IncidentService;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.config.ConfigUtil;
import me.morpheus.metropolis.configurate.serialize.Object2IntSerializer;
import me.morpheus.metropolis.configurate.serialize.Reference2DoubleSerializer;
import me.morpheus.metropolis.configurate.serialize.Reference2IntSerializer;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TownTypeLoader implements CustomResourceLoader<TownType> {

    private static final Path TOWN_TYPE = ConfigUtil.CUSTOM.resolve("town-type");

    @Override
    public String getId() {
        return "town_type";
    }

    @Override
    public String getName() {
        return "TownType";
    }

    public Collection<TownType> load() {
        if (Files.exists(TownTypeLoader.TOWN_TYPE)) {
            final List<TownType> townTypes = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(TownTypeLoader.TOWN_TYPE)) {
                for (Path file : stream) {
                    MPLog.getLogger().info("Loading townType from {}", file.getFileName());
                    townTypes.add(load(file));
                }
                return townTypes;
            } catch (Exception e) {
                Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                        .create(new MPIncident(MPGenericErrors.config(), e));
                return Collections.emptyList();
            }
        }

        final Reference2DoubleMap<PlotType> prices = new Reference2DoubleOpenHashMap<>();
        prices.put(PlotTypes.HOMEBLOCK, 0);
        prices.put(PlotTypes.PLOT, 10);
        prices.put(PlotTypes.OUTPOST, 100);

        final Reference2IntMap<PlotType> caps = new Reference2IntOpenHashMap<>();
        caps.put(PlotTypes.HOMEBLOCK, 1);
        caps.put(PlotTypes.PLOT, 100);
        caps.put(PlotTypes.OUTPOST, 5);

        return Collections.singletonList(
                new MPTownType("settlement", "Settlement", "", 1, 20, prices, caps)
        );
    }

    @Override
    public TownType load(Path path) throws IOException, ObjectMappingException {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(Object2IntMap.class), new Object2IntSerializer());
        serializers.registerType(TypeToken.of(Reference2IntMap.class), new Reference2IntSerializer());
        serializers.registerType(TypeToken.of(Reference2DoubleMap.class), new Reference2DoubleSerializer());

        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(options)
                .setPath(path)
                .build();

        ObjectMapper.BoundInstance mapper = ObjectMapper.forClass(MPTownType.class).bindToNew();
        CommentedConfigurationNode node = loader.load();
        mapper.populate(node);

        SimpleCommentedConfigurationNode n = SimpleCommentedConfigurationNode.root();
        mapper.serialize(n);
        loader.save(n);

        return (TownType) mapper.getInstance();
    }

    @Override
    public CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            if (Files.notExists(TownTypeLoader.TOWN_TYPE)) {
                try {
                    Files.createDirectories(TownTypeLoader.TOWN_TYPE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            final Collection<? extends CatalogType> types = Sponge.getRegistry().getAllOf(TownType.class);
            for (CatalogType catalogType : types) {
                final Path save = TownTypeLoader.TOWN_TYPE.resolve(catalogType.getId() + ".conf");
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

