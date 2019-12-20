package me.morpheus.metropolis.api.event.town;

import javax.annotation.Nullable;
import java.util.function.DoubleSupplier;

public interface TownTransactionEvent extends TargetTownEvent {

    void addSupplier(String key, DoubleSupplier supplier);

    @Nullable
    DoubleSupplier getSupplier(String key);

    interface Upkeep extends TownTransactionEvent {}

}
