package me.morpheus.metropolis.api.custom;

import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.town.TownType;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class CustomResourceLoaders {

    public static final CustomResourceLoader<Rank> RANK = DummyObjectProvider.createFor(CustomResourceLoader.class, "RANK");
    public static final CustomResourceLoader<TownType> TOWN_TYPE = DummyObjectProvider.createFor(CustomResourceLoader.class, "TOWN_TYPE");

    private CustomResourceLoaders() {}
}
