package me.morpheus.metropolis.api.plot;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface PlotService {

    Stream<Plot> plots();

    Stream<Plot> plots(UUID world);

    Plot create(Town town);

    Optional<Plot> get(Location<World> loc);

    Optional<Plot> claim(Location<World> loc, Plot plot);

    Optional<Plot> unclaim(Location<World> loc);

    void unclaim(Predicate<Plot> predicate);

    void unclaim(UUID world, Predicate<Plot> predicate);

    boolean hasPermission(User user, Plot plot, Flag flag);

    boolean hasPermission(User user, CitizenData cd, Plot plot, Flag flag);

    CompletableFuture<Void> saveAll();

    CompletableFuture<Void> loadAll();

    void registerListeners();

}
