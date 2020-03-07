package me.morpheus.metropolis.town.upgrade;

import me.morpheus.metropolis.api.custom.CustomResourceLoaders;
import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.TownType;
import me.morpheus.metropolis.api.town.Upgrade;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
import me.morpheus.metropolis.api.town.pvp.PvPOptions;
import me.morpheus.metropolis.custom.CustomResourceLoaderRegistryModule;
import me.morpheus.metropolis.plot.PlotTypeRegistryModule;
import me.morpheus.metropolis.town.type.TownTypeRegistryModule;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({CustomResourceLoaderRegistryModule.class, TownTypeRegistryModule.class})
public final class UpgradeRegistryModule implements AdditionalCatalogRegistryModule<Upgrade> {

    private final Map<String, Upgrade> map = new HashMap<>(4);

    @Override
    public void registerDefaults() {
        final Collection<Upgrade> upgrades = CustomResourceLoaders.UPGRADE.load();

        for (Upgrade upgrade : upgrades) {
            register(upgrade);
        }
    }

    private void register(Upgrade type) {
        this.map.put(type.getId(), type);
    }

    @Override
    public void registerAdditionalCatalog(Upgrade extraCatalog) {
        this.map.putIfAbsent(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<Upgrade> getById(String id) {
        return Optional.ofNullable(this.map.get(id));
    }

    @Override
    public Collection<Upgrade> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }
}
