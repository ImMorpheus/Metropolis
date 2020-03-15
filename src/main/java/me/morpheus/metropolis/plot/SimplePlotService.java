package me.morpheus.metropolis.plot;

import com.flowpowered.math.vector.Vector2i;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.data.plot.ImmutablePlotData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.config.ConfigUtil;
import me.morpheus.metropolis.plot.listeners.InternalChangeBlockHandler;
import me.morpheus.metropolis.plot.listeners.InternalClaimHandler;
import me.morpheus.metropolis.plot.listeners.InternalDamageEntityHandler;
import me.morpheus.metropolis.plot.listeners.InternalExplosionTownHandler;
import me.morpheus.metropolis.plot.listeners.InternalInteractHandler;
import me.morpheus.metropolis.plot.listeners.InternalLoginHandler;
import me.morpheus.metropolis.plot.listeners.InternalMoveEntityHandler;
import me.morpheus.metropolis.plot.listeners.InternalNotifyHandler;
import me.morpheus.metropolis.plot.listeners.InternalSpawnEntityHandler;
import me.morpheus.metropolis.util.VectorUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SimplePlotService implements PlotService {

    private final Map<UUID, Map<Vector2i, PlotData>> map = new HashMap<>();
    private final Map<UUID, Set<Vector2i>> deleted = new HashMap<>();
    private boolean loaded = false;

    private static final Path PLOT_DATA = ConfigUtil.DATA.resolve("plot");

    @Override
    public Stream<PlotData> plots() {
        return this.map.values().stream().flatMap(m -> m.values().stream());
    }

    @Override
    public Stream<PlotData> plots(UUID world) {
        final Map<Vector2i, PlotData> plots = this.map.get(world);
        if (plots == null) {
            return Stream.empty();
        }
        return plots.values().stream();
    }

    @Override
    public Optional<PlotData> get(Location<World> loc) {
        final Vector2i cp = VectorUtil.toChunk2i(loc);
        final UUID world = loc.getExtent().getUniqueId();

        return Optional.ofNullable(get(world, cp));
    }

    @Nullable
    public PlotData get(UUID world, Vector2i cp) {
        final Map<Vector2i, PlotData> plots = this.map.get(world);
        if (plots == null) {
            return null;
        }
        return plots.get(cp);
    }

    @Override
    public Optional<PlotData> claim(Location<World> loc, PlotData pd) {
        final Vector2i cp = VectorUtil.toChunk2i(loc);
        final UUID world = loc.getExtent().getUniqueId();

        return Optional.ofNullable(claim(world, cp, pd));
    }

    @Override
    public Optional<PlotData> unclaim(Location<World> loc) {
        final Vector2i cp = VectorUtil.toChunk2i(loc);
        final UUID world = loc.getExtent().getUniqueId();

        final Map<Vector2i, PlotData> plots = this.map.get(world);

        if (plots == null) {
            return Optional.empty();
        }
        final Set<Vector2i> w = this.deleted.computeIfAbsent(world, k -> new HashSet<>());
        w.add(cp);

        return Optional.ofNullable(plots.remove(cp));
    }

    @Override
    public void unclaim(Predicate<PlotData> predicate) {
        for (Map.Entry<UUID, Map<Vector2i, PlotData>> world : this.map.entrySet()) {
            final Iterator<Map.Entry<Vector2i, PlotData>> iterator = world.getValue().entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<Vector2i, PlotData> e = iterator.next();
                if (predicate.test(e.getValue())) {
                    final Set<Vector2i> w = this.deleted.computeIfAbsent(world.getKey(), k -> new HashSet<>());
                    w.add(e.getKey());
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void unclaim(UUID world, Predicate<PlotData> predicate) {
        final Map<Vector2i, PlotData> plots = this.map.get(world);
        if (plots != null) {
            final Iterator<Map.Entry<Vector2i, PlotData>> iterator = plots.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<Vector2i, PlotData> e = iterator.next();
                if (predicate.test(e.getValue())) {
                    final Set<Vector2i> w = this.deleted.computeIfAbsent(world, k -> new HashSet<>());
                    w.add(e.getKey());
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public CompletableFuture<Void> saveAll() {
        if (this.map.isEmpty() && this.deleted.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            if (Files.notExists(SimplePlotService.PLOT_DATA)) {
                try {
                    Files.createDirectories(SimplePlotService.PLOT_DATA);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }
            for (Map.Entry<UUID, Map<Vector2i, PlotData>> entry : this.map.entrySet()) {
                final UUID uuid = entry.getKey();
                final Path worldDir = SimplePlotService.PLOT_DATA.resolve(uuid.toString());

                if (Files.notExists(worldDir)) {
                    try {
                        Files.createDirectory(worldDir);
                    } catch (IOException e) {
                        MPLog.getLogger().error("Unable to create world dir at {}", worldDir);
                        MPLog.getLogger().error("Error:", e);
                    }
                }

                for (Map.Entry<Vector2i, PlotData> worldEntry : entry.getValue().entrySet()) {
                    final PlotData pd = worldEntry.getValue();
                    final Vector2i plot = worldEntry.getKey();

                    final String name = plot.getX() + "." + plot.getY();
                    final Path tmp = worldDir.resolve(name + ".tmp");

                    final DataContainer container = pd.toContainer();

                    try (OutputStream out = Files.newOutputStream(tmp, StandardOpenOption.CREATE)) {
                        DataFormats.JSON.writeTo(out, container);
                        Files.move(tmp, worldDir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        MPLog.getLogger().error("Unable to save Plot at {}", plot);
                        MPLog.getLogger().error("Error:", e);
                    }
                }
            }

            for (Map.Entry<UUID, Set<Vector2i>> entry : this.deleted.entrySet()) {
                final UUID uuid = entry.getKey();
                final Path worldDir = SimplePlotService.PLOT_DATA.resolve(uuid.toString());
                final Iterator<Vector2i> iterator = entry.getValue().iterator();

                while (iterator.hasNext()) {
                    final Vector2i plot = iterator.next();
                    final String name = plot.getX() + "." + plot.getY();
                    try {
                        Files.deleteIfExists(worldDir.resolve(name));
                    } catch (IOException e) {
                        MPLog.getLogger().error("Unable to remove deleted Plot at {}", name);
                        MPLog.getLogger().error("Error:", e);
                    }
                    iterator.remove();
                }
            }
        });
    }

    @Override
    public CompletableFuture<Void> loadAll() {
        if (Files.notExists(SimplePlotService.PLOT_DATA)) {
            this.loaded = true;
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            final DataManipulatorBuilder<PlotData, ImmutablePlotData> builder = Sponge.getDataManager().getManipulatorBuilder(PlotData.class).get();

            try (DirectoryStream<Path> worlds = Files.newDirectoryStream(SimplePlotService.PLOT_DATA)) {
                for (Path world : worlds) {
                    final UUID uuid = UUID.fromString(world.getFileName().toString());
                    try (DirectoryStream<Path> plots = Files.newDirectoryStream(world)) {
                        for (Path plot : plots) {
                            final String[] name = plot.getFileName().toString().split("\\.");
                            final Vector2i coord = Vector2i.from(Integer.parseInt(name[0]), Integer.parseInt(name[1]));

                            try (InputStream in = Files.newInputStream(plot)) {
                                final DataContainer container = DataFormats.JSON.readFrom(in);
                                final PlotData plotData = builder.build(container)
                                        .orElseThrow(() -> new InvalidDataException(container.toString()));
                                claim(uuid, coord, plotData);
                            }
                        }
                    }
                }
                this.loaded = true;
            } catch (IOException e) {
                this.loaded = true;
                throw new CompletionException(e);
            }
        });
    }

    @Override
    public void registerListeners() {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        Sponge.getEventManager().registerListeners(plugin, new InternalChangeBlockHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalClaimHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalDamageEntityHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalExplosionTownHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalInteractHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalMoveEntityHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalNotifyHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalLoginHandler(this));
        Sponge.getEventManager().registerListeners(plugin, new InternalSpawnEntityHandler(this));
    }

    @Nullable
    private PlotData claim(final UUID world, final Vector2i chunk, final PlotData pd) {
        final Map<Vector2i, PlotData> plots = this.map.computeIfAbsent(world, k -> new HashMap<>());
        return plots.putIfAbsent(chunk, pd);
    }

    public boolean isLoadedCompleted() {
        return this.loaded;
    }
}
