package me.morpheus.metropolis.plot;

import me.morpheus.metropolis.api.plot.PlotService;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.plot.PlotTypes;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class PlotTypeRegistryModule implements AdditionalCatalogRegistryModule<PlotType> {

    @RegisterCatalog(PlotTypes.class)
    private final Map<String, PlotType> map = new HashMap<>(3);

    @Override
    public void registerDefaults() {
        register(new MPPlotType(
                "plot",
                "plot",
                (t, loc) -> Sponge.getServiceManager().provideUnchecked(PlotService.class)
                        .testNear(loc, plot -> plot != null && plot.town().get().intValue() == t.getId(), false)));
        register(new MPPlotType(
                "homeblock",
                "homeblock",
                (t, loc) -> false));
        register(new MPPlotType(
                "outpost",
                "outpost",
                (t, loc) -> !Sponge.getServiceManager().provideUnchecked(PlotService.class)
                        .testNear(loc, plot -> plot != null && plot.town().get().intValue() == t.getId(), false)));
    }

    private void register(PlotType type) {
        this.map.put(type.getId(), type);
    }

    @Override
    public void registerAdditionalCatalog(PlotType extraCatalog) {
        this.map.putIfAbsent(extraCatalog.getId(), extraCatalog);
    }

    @Override
    public Optional<PlotType> getById(String id) {
        return Optional.ofNullable(this.map.get(id));
    }

    @Override
    public Collection<PlotType> getAll() {
        return Collections.unmodifiableCollection(this.map.values());
    }
}

