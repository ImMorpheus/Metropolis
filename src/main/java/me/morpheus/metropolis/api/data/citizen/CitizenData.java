package me.morpheus.metropolis.api.data.citizen;

import me.morpheus.metropolis.api.rank.Rank;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.time.Instant;
import java.util.UUID;

public interface CitizenData extends DataManipulator<CitizenData, ImmutableCitizenData> {

    Value<Integer> town();

    Value<Rank> rank();

    SetValue<UUID> friends();

    Value<Instant> joined();

}
