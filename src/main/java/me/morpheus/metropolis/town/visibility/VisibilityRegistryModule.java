package me.morpheus.metropolis.town.visibility;

import me.morpheus.metropolis.api.town.visibility.Visibilities;
import me.morpheus.metropolis.api.town.visibility.Visibility;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VisibilityRegistryModule implements AdditionalCatalogRegistryModule<Visibility> {

    @RegisterCatalog(Visibilities.class)
    private final Map<String, Visibility> map = new HashMap<>(3);

    @Override
    public void registerDefaults() {
        register(new MPVisibility("public", "Public"));
        register(new MPVisibility("private", "Private"));
    }

    private void register(Visibility type) {
        this.map.put(type.getId(), type);
    }

    @Override
    public void registerAdditionalCatalog(Visibility extraCatalog) {
        this.map.putIfAbsent(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<Visibility> getById(String id) {
        return Optional.ofNullable(this.map.get(id));
    }

    @Override
    public Collection<Visibility> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }
}
