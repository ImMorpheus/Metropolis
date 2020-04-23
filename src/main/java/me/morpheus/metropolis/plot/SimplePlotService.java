package me.morpheus.metropolis.plot;

import com.flowpowered.math.vector.Vector2i;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.config.ConfigService;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.Plot;
import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.Town;
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
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.user.UserStorageService;
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

public final class SimplePlotService implements PlotService {

    private final Map<UUID, Map<Vector2i, Plot>> map = new HashMap<>();
    private final Map<UUID, Set<Vector2i>> deleted = new HashMap<>();
    private boolean loaded = false;

    private static final Path PLOT_DATA = ConfigUtil.DATA.resolve("plot");

    @Override
    public Stream<Plot> plots() {
        return this.map.values().stream().flatMap(m -> m.values().stream());
    }

    @Override
    public Stream<Plot> plots(UUID world) {
        final Map<Vector2i, Plot> plots = get(world);
        if (plots == null) {
            return Stream.empty();
        }
        return plots.values().stream();
    }

    @Override
    public Plot create(Town town) {
        return new MPPlot(town);
    }

    @Override
    public Optional<Plot> get(Location<World> loc) {
        final Vector2i cp = VectorUtil.toChunk2i(loc);
        final UUID world = loc.getExtent().getUniqueId();

        return Optional.ofNullable(get(world, cp));
    }

    @Override
    public Optional<Plot> claim(Location<World> loc, Plot plot) {
        final Vector2i cp = VectorUtil.toChunk2i(loc);
        final UUID world = loc.getExtent().getUniqueId();
        return Optional.ofNullable(claim(world, cp, plot));
    }

    @Override
    public Optional<Plot> unclaim(Location<World> loc) {
        final Vector2i cp = VectorUtil.toChunk2i(loc);
        final UUID world = loc.getExtent().getUniqueId();

        final Map<Vector2i, Plot> plots = get(world);

        if (plots == null) {
            return Optional.empty();
        }
        final Set<Vector2i> w = this.deleted.computeIfAbsent(world, k -> new HashSet<>());
        w.add(cp);

        return Optional.ofNullable(plots.remove(cp));
    }

    @Override
    public void unclaim(Predicate<Plot> predicate) {
        for (Map.Entry<UUID, Map<Vector2i, Plot>> world : this.map.entrySet()) {
            final Iterator<Map.Entry<Vector2i, Plot>> iterator = world.getValue().entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<Vector2i, Plot> e = iterator.next();
                if (predicate.test(e.getValue())) {
                    final Set<Vector2i> w = this.deleted.computeIfAbsent(world.getKey(), k -> new HashSet<>());
                    w.add(e.getKey());
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void unclaim(UUID world, Predicate<Plot> predicate) {
        final Map<Vector2i, Plot> plots = get(world);
        if (plots != null) {
            final Iterator<Map.Entry<Vector2i, Plot>> iterator = plots.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<Vector2i, Plot> e = iterator.next();
                if (predicate.test(e.getValue())) {
                    final Set<Vector2i> w = this.deleted.computeIfAbsent(world, k -> new HashSet<>());
                    w.add(e.getKey());
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public boolean hasPermission(User source, Plot plot, Flag flag) {
        final Optional<CitizenData> cdOpt = source.get(CitizenData.class);
        return cdOpt.isPresent() && hasPermission(source, cdOpt.get(), plot, flag);
    }

    @Override
    public boolean hasPermission(User source, CitizenData cd, Plot plot, Flag flag) {
        if (plot.getTown() != cd.town().get().intValue()) {
            return false;
        }

        if (!plot.getOwner().isPresent()) {
            final byte value = plot.getPermission(flag);
            if (value != Byte.MIN_VALUE) {
                return hasPermissionAdvanced(value, cd, flag);
            }
            return hasPermissionInUnowned(cd, flag);
        }

        final UUID ownerId = plot.getOwner().get();
        if (source.getUniqueId().equals(ownerId)) {
            return true;
        }

        final byte perm = cd.rank().get().getPermission(flag);
        final byte value = plot.getPermission(flag);
        if (value != Byte.MIN_VALUE) {
            if (perm >= value) {
                return true;
            }
        }

        final Optional<User> ownerOpt = Sponge.getServiceManager().provideUnchecked(UserStorageService.class).get(ownerId);
        if (ownerOpt.isPresent()) {
            final User owner = ownerOpt.get();
            final Optional<CitizenData> cdOpt = owner.get(CitizenData.class);
            if (cdOpt.isPresent() && cdOpt.get().town().get().intValue() == plot.getTown()) {
                if (cdOpt.get().friends().contains(source.getUniqueId())) {
                    return true;
                }
                if (value != Byte.MIN_VALUE) {
                    return false;
                }
                return perm > cdOpt.get().rank().get().getPermission(flag);
            }
        }
        return hasPermissionInUnowned(cd, flag);
    }

    private boolean hasPermissionAdvanced(byte value, CitizenData cd, Flag flag) {
        final Rank rank = cd.rank().get();
        return rank.getPermission(flag) >= value;
    }

    private boolean hasPermissionInUnowned(CitizenData cd, Flag flag) {
        final Rank rank = cd.rank().get();
        final GlobalConfig config = Sponge.getServiceManager().provideUnchecked(ConfigService.class).getGlobal();
        return rank.getPermission(flag) > config.getTownCategory().getPlotCategory().getUnownedPermission(flag);
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
            for (Map.Entry<UUID, Map<Vector2i, Plot>> entry : this.map.entrySet()) {
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

                for (Map.Entry<Vector2i, Plot> worldEntry : entry.getValue().entrySet()) {
                    final Plot plot = worldEntry.getValue();
                    final Vector2i coord = worldEntry.getKey();

                    final String name = coord.getX() + "." + coord.getY();
                    final Path tmp = worldDir.resolve(name + ".tmp");

                    final DataContainer container = plot.toContainer();

                    try (OutputStream out = Files.newOutputStream(tmp, StandardOpenOption.CREATE)) {
                        DataFormats.JSON.writeTo(out, container);
                        Files.move(tmp, worldDir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        MPLog.getLogger().error("Unable to save Plot at {}", coord);
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
            try (DirectoryStream<Path> worlds = Files.newDirectoryStream(SimplePlotService.PLOT_DATA)) {
                for (Path world : worlds) {
                    final UUID uuid = UUID.fromString(world.getFileName().toString());
                    try (DirectoryStream<Path> plots = Files.newDirectoryStream(world)) {
                        for (Path plot : plots) {
                            final String[] name = plot.getFileName().toString().split("\\.");
                            final Vector2i coord = Vector2i.from(Integer.parseInt(name[0]), Integer.parseInt(name[1]));

                            try (InputStream in = Files.newInputStream(plot)) {
                                final DataContainer container = DataFormats.JSON.readFrom(in);
                                final Plot p = MPPlot.from(container)
                                        .orElseThrow(() -> new InvalidDataException(container.toString()));
                                claim(uuid, coord, p);
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
    public Map<Vector2i, Plot> get(UUID world) {
        return this.map.get(world);
    }

    @Nullable
    public Plot get(UUID world, Vector2i cp) {
        final Map<Vector2i, Plot> plots = get(world);
        if (plots == null) {
            return null;
        }
        return plots.get(cp);
    }

    @Nullable
    private Plot claim(final UUID world, final Vector2i chunk, final Plot plot) {
        final Map<Vector2i, Plot> plots = this.map.computeIfAbsent(world, k -> new HashMap<>());
        return plots.put(chunk, plot);
    }

    public boolean isReady() {
        return this.loaded;
    }
}
