package me.morpheus.metropolis.api.data.plot;

import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.plot.PlotType;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public interface ImmutablePlotData extends ImmutableDataManipulator<ImmutablePlotData, PlotData> {

    ImmutableValue<Integer> town();

    ImmutableValue<Text> name();

    ImmutableOptionalValue<UUID> owner();

    ImmutableValue<Double> price();

    ImmutableValue<Double> rent();

    ImmutableValue<Boolean> forSale();

    ImmutableValue<PlotType> type();

    /**
     * Returns true if mob spawn is permitted.
     * @return true if mob can spawn in plot, false otherwise.
     */
    ImmutableValue<Boolean> mobSpawn();

    byte getPermission(Flag flag);

}
