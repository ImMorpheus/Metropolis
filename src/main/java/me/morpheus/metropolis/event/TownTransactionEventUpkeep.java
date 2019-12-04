package me.morpheus.metropolis.event;

import it.unimi.dsi.fastutil.objects.Object2DoubleFunction;
import me.morpheus.metropolis.api.event.TownTransactionEvent;
import me.morpheus.metropolis.api.town.Town;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

public final class TownTransactionEventUpkeep implements TownTransactionEvent.Upkeep {

    private final Cause cause;
    private final Town town;
    private final Map<String, DoubleSupplier> map = new HashMap<>();


    public TownTransactionEventUpkeep(Cause cause, Town town) {
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
