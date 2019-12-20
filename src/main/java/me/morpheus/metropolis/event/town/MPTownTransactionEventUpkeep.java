package me.morpheus.metropolis.event.town;

import me.morpheus.metropolis.api.event.town.TownTransactionEvent;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.event.cause.Cause;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public final class MPTownTransactionEventUpkeep implements TownTransactionEvent.Upkeep {

    private final Cause cause;
    private final Town town;
    private final Map<String, DoubleSupplier> map = new HashMap<>();


    public MPTownTransactionEventUpkeep(Cause cause, Town town) {
        this.cause = cause;
        this.town = town;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Town getTargetTown() {
        return this.town;
    }

    @Override
    public void addSupplier(String key, DoubleSupplier supplier) {
        this.map.put(key, supplier);
    }

    @Override
    public DoubleSupplier getSupplier(String key) {
        return this.map.get(key);
    }

    public Map<String, DoubleSupplier> getMap() {
        return this.map;
    }
}
