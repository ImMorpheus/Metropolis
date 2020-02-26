package me.morpheus.metropolis.api.plot;

import me.morpheus.metropolis.api.data.plot.PlotData;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface PlotService {

    Stream<PlotData> plots();

    Stream<PlotData> plots(UUID world);

    Optional<PlotData> get(Location<World> loc);

    Optional<PlotData> claim(Location<World> loc, PlotData pd);

    Optional<PlotData> unclaim(Location<World> loc);

    void unclaim(Predicate<PlotData> predicate);

    void unclaim(UUID world, Predicate<PlotData> predicate);

    CompletableFuture<Void> saveAll();

    CompletableFuture<Void> loadAll();

    void registerListeners();

}
