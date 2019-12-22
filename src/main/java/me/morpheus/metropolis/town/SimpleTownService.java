package me.morpheus.metropolis.town;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.morpheus.metropolis.Metropolis;
import me.morpheus.metropolis.api.town.TownTypes;
import me.morpheus.metropolis.town.listeners.InternalTownHandler;
import me.morpheus.metropolis.util.Hacks;
import me.morpheus.metropolis.MPLog;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.TownService;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
import me.morpheus.metropolis.api.town.visibility.Visibility;
import me.morpheus.metropolis.config.ConfigUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class SimpleTownService implements TownService {

    private int lastID = Integer.MIN_VALUE + 1;
    private final Int2ObjectMap<Town> towns = new Int2ObjectOpenHashMap<>();
    private final IntSet deleted = new IntOpenHashSet();

    private static final Path TOWN_DATA = ConfigUtil.DATA.resolve("town");

    static {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(Metropolis.ID).get();
        Sponge.getEventManager().registerListeners(plugin, new InternalTownHandler());
    }

    @Override
    public Optional<Town> create(Text name, Location<World> spawn) {
        final int id = this.lastID;
        final Town t = new MPTown(id, TownTypes.SETTLEMENT, name, spawn, Instant.now());
        this.lastID++;
        if (this.towns.containsKey(id)) {
            return Optional.empty();
        }
        this.towns.put(id, t);
        return Optional.of(t);
    }

    @Override
    public Optional<Town> delete(int id) {
        final Town t = this.towns.remove(id);
        this.deleted.add(id);
        return Optional.ofNullable(t);
    }

    private void register(Town town) {
        if (town.getId() == Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Unable to register a town with id Integer.MIN_VALUE");
        }

        final Town t = this.towns.put(town.getId(), town);
        if (t != null) {
            MPLog.getLogger().error("Duplicate id ({}) for {} and {}", town.getId(), town.getName(), t.getName());
            throw new IllegalStateException("Duplicate id ");
        }
        if (this.lastID <= town.getId()) {
            this.lastID = town.getId() + 1;
        }
    }

    @Override
    public Optional<Town> get(int id) {
        return Optional.ofNullable(this.towns.get(id));
    }

    @Override
    public boolean exist(int id) {
        return this.towns.containsKey(id);
    }

    @Override
    public Stream<Town> towns() {
        return this.towns.values().stream();
    }

    @Override
    public CompletableFuture<Void> save(Town town) {
        return CompletableFuture.runAsync(() -> internal$save(town));
    }

    @Override
    public CompletableFuture<Void> saveAll() {
        return CompletableFuture.runAsync(() -> {
            if (Files.notExists(SimpleTownService.TOWN_DATA)) {
                try {
                    Files.createDirectories(SimpleTownService.TOWN_DATA);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for (Town town : this.towns.values()) {
                if (((MPTown) town).isDirty()) {
                    internal$save(town);
                }
            }
            final IntIterator iterator = this.deleted.iterator();

            while (iterator.hasNext()) {
                final String current = Integer.toString(iterator.nextInt());
                try {
                    Files.deleteIfExists(SimpleTownService.TOWN_DATA.resolve(current));
                    iterator.remove();
                } catch (Exception e) {
                    MPLog.getLogger().error("Unable to remove deleted Town at {}", current);
                    MPLog.getLogger().error("Error:", e);
                }
            }
        });
    }

    private void internal$save(Town town) {
        final String id = Integer.toString(town.getId());

        final Path save = SimpleTownService.TOWN_DATA.resolve(id);
        final Path tmp = SimpleTownService.TOWN_DATA.resolve(id + ".tmp");
        final DataContainer container = town.toContainer();

        try (OutputStream out = Files.newOutputStream(tmp, StandardOpenOption.CREATE)) {
            DataFormats.JSON.writeTo(out, container);
            Files.move(tmp, save, StandardCopyOption.REPLACE_EXISTING);
            ((MPTown) town).setDirty(false);
        } catch (Exception e) {
            MPLog.getLogger().error("Unable to save Town at {}", save);
            MPLog.getLogger().error("Error:", e);
        }
    }

    @Override
    public CompletableFuture<Void> loadAll() {
        return CompletableFuture.runAsync(() -> {
            if (Files.exists(SimpleTownService.TOWN_DATA)) {
                try (Stream<Path> towns = Files.list(SimpleTownService.TOWN_DATA)) {
                    towns.forEach(town -> {
                        try (InputStream in = Files.newInputStream(town)) {
                            final DataContainer container = DataFormats.JSON.readFrom(in);
                            final Town t = from(container);
                            register(t);
                        } catch (Exception e) {
                            MPLog.getLogger().error("Unable to load Town ({})", town);
                            MPLog.getLogger().error("Error: ", e);
                        }
                    });
                } catch (IOException e) {
                    MPLog.getLogger().error("Unable to load Towns ", e);
                }
            }
        });
    }

    @Override
    public Town from(DataView view) {
        final int id = view.getInt(DataQuery.of("id")).get();
        final Instant founded = view.getObject(DataQuery.of("founded"), Instant.class).get();
        final TownType type = view.getCatalogType(DataQuery.of("type"), TownType.class).get();
        final Text name = view.getSerializable(DataQuery.of("name"), Text.class).get();
        final Text tag = view.getSerializable(DataQuery.of("tag"), Text.class).orElse(Text.of(name));
        final PvPOption pvp = view.getCatalogType(DataQuery.of("pvp"), PvPOption.class).get();
        final Location spawn = view.getSerializable(DataQuery.of("spawn"), Location.class).get();
        final Visibility visibility = view.getCatalogType(DataQuery.of("visibility"), Visibility.class).get();

        final MPTown town = new MPTown(id, type, name, spawn, founded);
        town.setTag(tag);

        town.setPvP(pvp);
        town.setVisibility(visibility);

        final Optional<List<DataView>> v = view.getViewList(Hacks.DATA_MANIPULATORS);

        if (v.isPresent()) {
            for (DataManipulator<?, ?> manipulator : Hacks.deserializeManipulatorList(v.get())) {
                town.offer(manipulator);
            }
        }
        town.setPlots(view.getInt(DataQuery.of("plots")).orElse(0));
        town.setCitizens(view.getInt(DataQuery.of("citizens")).orElse(0));

        return town;
    }
}
