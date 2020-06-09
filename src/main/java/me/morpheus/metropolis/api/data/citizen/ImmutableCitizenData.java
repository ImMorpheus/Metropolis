package me.morpheus.metropolis.api.data.citizen;

import me.morpheus.metropolis.api.rank.Rank;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.immutable.ImmutableSetValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.time.Instant;
import java.util.UUID;

public interface ImmutableCitizenData extends ImmutableDataManipulator<ImmutableCitizenData, CitizenData> {

    ImmutableValue<Integer> town();

    ImmutableValue<Rank> rank();

    ImmutableSetValue<UUID> friends();

    ImmutableValue<Instant> joined();

}
