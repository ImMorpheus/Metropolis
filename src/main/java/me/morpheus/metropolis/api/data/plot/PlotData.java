package me.morpheus.metropolis.api.data.plot;

import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.PlotType;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public interface PlotData extends DataManipulator<PlotData, ImmutablePlotData> {

    Value<Integer> town();

    Value<Text> name();

    OptionalValue<UUID> owner();

    Value<Double> price();

    Value<Double> rent();

    Value<Boolean> forSale();

    Value<PlotType> type();

    /**
     * Returns true if mob spawn is permitted.
     * @return true if mob can spawn in plot, false otherwise.
     */
    Value<Boolean> mobSpawn();

    byte getPermission(Flag flag);

    void addPermission(Flag flag, byte value);

}
