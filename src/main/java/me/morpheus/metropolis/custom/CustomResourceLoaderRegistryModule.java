package me.morpheus.metropolis.custom;

import me.morpheus.metropolis.api.custom.CustomResourceLoader;
import me.morpheus.metropolis.api.custom.CustomResourceLoaders;
import me.morpheus.metropolis.rank.RankLoader;
import me.morpheus.metropolis.town.type.TownTypeLoader;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CustomResourceLoaderRegistryModule implements AdditionalCatalogRegistryModule<CustomResourceLoader> {

    @RegisterCatalog(CustomResourceLoaders.class)
    private final Map<String, CustomResourceLoader> map = new HashMap<>(3);

    @Override
    public void registerDefaults() {
        register(new RankLoader());
        register(new TownTypeLoader());
    }

    private void register(CustomResourceLoader loader) {
        this.map.put(loader.getId(), loader);
    }

    @Override
    public void registerAdditionalCatalog(CustomResourceLoader extraCatalog) {
        this.map.putIfAbsent(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<CustomResourceLoader> getById(String id) {
        return Optional.ofNullable(this.map.get(id));
    }

    @Override
    public Collection<CustomResourceLoader> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }
}
