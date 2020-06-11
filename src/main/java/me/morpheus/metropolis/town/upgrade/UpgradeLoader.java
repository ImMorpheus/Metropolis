package me.morpheus.metropolis.town.upgrade;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ShortMap;
import it.unimi.dsi.fastutil.objects.Reference2ShortOpenHashMap;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.health.IncidentService;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.town.TownTypes;
import me.morpheus.metropolis.api.town.Upgrade;
import me.morpheus.metropolis.config.ConfigUtil;
import me.morpheus.metropolis.configurate.serialize.Reference2DoubleSerializer;
import me.morpheus.metropolis.configurate.serialize.Reference2ShortSerializer;
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
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class UpgradeLoader implements CustomResourceLoader<Upgrade> {

    private static final Path UPGRADE = ConfigUtil.CUSTOM.resolve("upgrade");

    @Override
    public String getId() {
        return "upgrade";
    }

    @Override
    public String getName() {
        return "Upgrade";
    }

    public Collection<Upgrade> load() {
        if (Files.exists(UpgradeLoader.UPGRADE)) {
            final List<Upgrade> upgrades = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(UpgradeLoader.UPGRADE)) {
                for (Path file : stream) {
                    MPLog.getLogger().info("Loading upgrade from {}", file.getFileName());
                    upgrades.add(load(file));
                }
                return upgrades;
            } catch (Exception e) {
                Sponge.getServiceManager().provideUnchecked(IncidentService.class)
                        .create(new MPIncident(MPGenericErrors.config(), e));
                return Collections.emptyList();
            }
        }
        final Reference2ShortMap<PlotType> max = new Reference2ShortOpenHashMap<>();
        max.defaultReturnValue(Short.MAX_VALUE);
        final Reference2ShortMap<PlotType> min = new Reference2ShortOpenHashMap<>();
        min.defaultReturnValue((short) 0);
        return Collections.singletonList(
                new MPUpgrade(
                        "dummyupgrade", "DummyUpgrade", Text.of("DummyDescription"), Collections.emptySet(),
                        TownTypes.SETTLEMENT, 0.0, Short.MAX_VALUE, (short) 0, max, min
                )
        );
    }

    @Override
    public Upgrade load(Path path) throws IOException, ObjectMappingException {
        TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers().newChild();
        serializers.registerType(TypeToken.of(Reference2ShortMap.class), new Reference2ShortSerializer());

        ConfigurationOptions options = ConfigurationOptions.defaults().setSerializers(serializers);
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setDefaultOptions(options)
                .setPath(path)
                .build();

        ObjectMapper<MPUpgrade>.BoundInstance mapper = ObjectMapper.forClass(MPUpgrade.class).bindToNew();
        CommentedConfigurationNode node = loader.load();
        mapper.populate(node);

        MPUpgrade upgrade = mapper.getInstance();
        upgrade.getMaxPlots().defaultReturnValue(Short.MAX_VALUE);
        upgrade.getMinPlots().defaultReturnValue((short) 0);

        return upgrade;
    }

    @Override
    public void save() {
        if (Files.notExists(UpgradeLoader.UPGRADE)) {
            try {
                Files.createDirectories(UpgradeLoader.UPGRADE);
            } catch (IOException e) {
                MPLog.getLogger().error("Error while creating save dir");
                MPLog.getLogger().error("Exception:", e);
                return;
            }
        }
        final Collection<? extends CatalogType> types = Sponge.getRegistry().getAllOf(Upgrade.class);
        for (CatalogType catalogType : types) {
            final Path save = UpgradeLoader.UPGRADE.resolve(catalogType.getId() + ".conf");
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
    }
}

