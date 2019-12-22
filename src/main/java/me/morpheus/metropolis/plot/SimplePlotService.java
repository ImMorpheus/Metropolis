package me.morpheus.metropolis.plot;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.data.plot.ImmutablePlotData;
import me.morpheus.metropolis.api.data.plot.PlotData;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.config.ConfigUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SimplePlotService implements PlotService {

    private final Map<UUID, Map<Vector2i, PlotData>> map = new HashMap<>();
    private final Map<UUID, Set<Vector2i>> deleted = new HashMap<>();

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
        final Vector2i cp = to2i(loc);
        final UUID world = loc.getExtent().getUniqueId();

        final Map<Vector2i, PlotData> plots = this.map.get(world);
        if (plots == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(plots.get(cp));
    }

    @Override
    public Stream<PlotData> get(Explosion explosion) {
        final Location<World> loc = explosion.getLocation();
        final Map<Vector2i, PlotData> plots = this.map.get(loc.getExtent().getUniqueId());
        if (plots == null) {
            return Stream.empty();
        }

        final float radius = explosion.getRadius();
        final Vector2i nw = to2i(loc.sub(radius, 0, radius));
        final Vector2i se = to2i(loc.add(radius, 0, radius));

        if (nw.equals(se)) {
            final Vector2i center = to2i(loc);
            return Stream.of(plots.get(center));
        }

        final List<PlotData> pds = new ArrayList<>();

        for (; nw.getY() < se.getY(); nw.add(Vector2i.UNIT_Y)) {
            for (; nw.getX() < se.getX(); nw.add(Vector2i.UNIT_X)) {
                final PlotData pd = plots.get(nw);
                if (pd != null) {
                    pds.add(pd);
                }
            }
        }

        return pds.stream();
    }

    @Override
    public Optional<PlotData> claim(Location<World> loc, PlotData pd) {
        final Vector2i cp = to2i(loc);
        final UUID world = loc.getExtent().getUniqueId();

        return Optional.ofNullable(claim(world, cp, pd));
    }

    @Override
    public Optional<PlotData> unclaim(Location<World> loc) {
        final Vector2i cp = to2i(loc);
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
    public boolean testNear(Location<World> loc, Predicate<PlotData> predicate, boolean and) {
        final UUID world = loc.getExtent().getUniqueId();
        final Map<Vector2i, PlotData> plots = this.map.get(world);

        if (plots == null) {
            return predicate.test(null);
        }
        int x = loc.getChunkPosition().getX();
        int z = loc.getChunkPosition().getZ();

        if (and) {
            return predicate.test(plots.get(Vector2i.from(x + 1, z)))
                    && predicate.test(plots.get(Vector2i.from(x - 1, z)))
                    && predicate.test(plots.get(Vector2i.from(x, z + 1)))
                    && predicate.test(plots.get(Vector2i.from(x, z - 1)));
        }

        return predicate.test(plots.get(Vector2i.from(x + 1, z)))
                || predicate.test(plots.get(Vector2i.from(x - 1, z)))
                || predicate.test(plots.get(Vector2i.from(x, z + 1)))
                || predicate.test(plots.get(Vector2i.from(x, z - 1)));
    }

    @Override
    public CompletableFuture<Void> saveAll() {
        return CompletableFuture.runAsync(() -> {
            if (Files.notExists(SimplePlotService.PLOT_DATA)) {
                try {
                    Files.createDirectories(SimplePlotService.PLOT_DATA);
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
        return CompletableFuture.runAsync(() -> {
            if (Files.exists(SimplePlotService.PLOT_DATA)) {
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
                                } catch (Exception e) {
                                    MPLog.getLogger().error("Unable to load plot {}", plot);
                                    MPLog.getLogger().error("Error: ", e);
                                }
                            }
                        } catch (Exception e) {
                            MPLog.getLogger().error("Unable to load plots for world {}", uuid);
                            MPLog.getLogger().error("Error: ", e);
                        }

                    }
                } catch (IOException e) {
                    MPLog.getLogger().error("Unable to load Plots ", e);
                }
            }
        });
    }

    @Nullable
    private PlotData claim(final UUID world, final Vector2i chunk, final PlotData pd) {
        final Map<Vector2i, PlotData> plots = this.map.computeIfAbsent(world, k -> new HashMap<>());
        return plots.putIfAbsent(chunk, pd);
    }

    private Vector2i to2i(final Location<World> loc) {
        final Vector3i cp = loc.getChunkPosition();
        return Vector2i.from(cp.getX(), cp.getZ());
    }
}
