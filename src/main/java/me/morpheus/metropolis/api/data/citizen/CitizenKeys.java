package me.morpheus.metropolis.api.data.citizen;

import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.util.MPTypeTokens;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.TypeTokens;

import java.time.Instant;
import java.util.UUID;

public final class CitizenKeys {

    public static final Key<Value<Integer>> TOWN = Key.builder()
            .id("town")
            .name("Town")
            .query(DataQuery.of("Town"))
            .type(TypeTokens.INTEGER_VALUE_TOKEN)
            .build();

    public static final Key<Value<Rank>> RANK = Key.builder()
            .id("rank")
            .name("Rank")
            .query(DataQuery.of("Rank"))
            .type(MPTypeTokens.RANK_VALUE_TOKEN)
            .build();

    public static final Key<SetValue<UUID>> FRIENDS = Key.builder()
            .id("friends")
            .name("Friends")
            .query(DataQuery.of("Friends"))
            .type(MPTypeTokens.SET_UUID_VALUE_TOKEN)
            .build();

    public static final Key<Value<Instant>> JOINED = Key.builder()
            .id("joined")
            .name("Joined")
            .query(DataQuery.of("Joined"))
            .type(TypeTokens.INSTANT_VALUE_TOKEN)
            .build();

    private CitizenKeys() {}
}
