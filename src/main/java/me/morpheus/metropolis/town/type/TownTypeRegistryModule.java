package me.morpheus.metropolis.town.type;

import me.morpheus.metropolis.api.custom.CustomResourceLoaders;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.TownTypes;
import me.morpheus.metropolis.custom.CustomResourceLoaderRegistryModule;
import me.morpheus.metropolis.plot.PlotTypeRegistryModule;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({CustomResourceLoaderRegistryModule.class, PlotTypeRegistryModule.class})
public final class TownTypeRegistryModule implements AdditionalCatalogRegistryModule<TownType> {

    @RegisterCatalog(TownTypes.class)
    private final Map<String, TownType> map = new HashMap<>(3);

    @Override
    public void registerDefaults() {
        final Collection<TownType> townTypes = CustomResourceLoaders.TOWN_TYPE.load();

        for (TownType townType : townTypes) {
            register(townType);
        }

    }

    private void register(TownType type) {
        this.map.put(type.getId(), type);
    }

    @Override
    public void registerAdditionalCatalog(TownType extraCatalog) {
        this.map.putIfAbsent(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<TownType> getById(String id) {
        return Optional.ofNullable(this.map.get(id));
    }

    @Override
    public Collection<TownType> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }
}

