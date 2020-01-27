package me.morpheus.metropolis.town.pvp;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.town.pvp.PvPOption;
import me.morpheus.metropolis.api.town.pvp.PvPOptions;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PvPOptionRegistryModule implements AdditionalCatalogRegistryModule<PvPOption> {

    @RegisterCatalog(PvPOptions.class)
    private final Map<String, PvPOption> map = new HashMap<>(4);

    @Override
    public void registerDefaults() {
        register(new MPPvPOption("on", "On", (source, target) -> true));
        register(new MPPvPOption("off", "Off", (source, target) -> false));
        register(new MPPvPOption("friendly_fire", "Friendly Fire", (source, target) -> {
            Optional<CitizenData> cdSopt = source.get(CitizenData.class);
            Optional<CitizenData> cdTopt = target.get(CitizenData.class);
            return cdSopt.isPresent() && cdTopt.isPresent() && cdSopt.get().town().get().intValue() == cdTopt.get().town().get().intValue();
        }));
        register(new MPPvPOption("grace_period", "Grace Period", (source, target) -> false));
    }

    private void register(PvPOption type) {
        this.map.put(type.getId(), type);
    }

    @Override
    public void registerAdditionalCatalog(PvPOption extraCatalog) {
        this.map.putIfAbsent(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<PvPOption> getById(String id) {
        return Optional.ofNullable(this.map.get(id));
    }

    @Override
    public Collection<PvPOption> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }
}
