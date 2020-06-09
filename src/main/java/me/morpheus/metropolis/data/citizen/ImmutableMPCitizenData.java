package me.morpheus.metropolis.data.citizen;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.ImmutableCitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.data.DataVersions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.immutable.ImmutableSetValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class ImmutableMPCitizenData extends AbstractImmutableData<ImmutableCitizenData, CitizenData> implements ImmutableCitizenData {

    private final int town;
    private final Rank rank;
    @Nullable private final Set<UUID> friends;
    private final Instant joined;

    ImmutableMPCitizenData(int town, Rank rank, @Nullable Set<UUID> friends, Instant joined) {
        this.town = town;
        this.rank = rank;
        this.friends = friends;
        this.joined = joined;
        registerGetters();
    }
    @Override
    protected void registerGetters() {
        registerKeyValue(CitizenKeys.TOWN, this::town);
        registerKeyValue(CitizenKeys.RANK, this::rank);
        registerKeyValue(CitizenKeys.FRIENDS, this::friends);
        registerKeyValue(CitizenKeys.JOINED, this::joined);

        registerFieldGetter(CitizenKeys.TOWN, this::getTown);
        registerFieldGetter(CitizenKeys.RANK, this::getRank);
        registerFieldGetter(CitizenKeys.FRIENDS, this::getFriends);
        registerFieldGetter(CitizenKeys.JOINED, this::getJoined);
    }

    @Override
    public CitizenData asMutable() {
        return new MPCitizenData(this.town, this.rank, this.friends, this.joined);
    }

    @Override
    public int getContentVersion() {
        return DataVersions.CitizenData.CURRENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = super.toContainer();
        container.set(CitizenKeys.TOWN.getQuery(), this.town);
        container.set(CitizenKeys.RANK.getQuery(), this.rank);
        if (this.friends != null && !this.friends.isEmpty()) {
            container.set(CitizenKeys.FRIENDS.getQuery(), this.friends);
        }
        container.set(CitizenKeys.JOINED.getQuery(), this.joined);

        return container;
    }

    @Override
    public ImmutableValue<Integer> town() {
        return Sponge.getRegistry().getValueFactory().createValue(CitizenKeys.TOWN, this.town).asImmutable();
    }

    @Override
    public ImmutableValue<Rank> rank() {
        return Sponge.getRegistry().getValueFactory().createValue(CitizenKeys.RANK, this.rank).asImmutable();
    }

    @Override
    public ImmutableSetValue<UUID> friends() {
        if (this.friends == null || this.friends.isEmpty()) {
            return Sponge.getRegistry().getValueFactory().createSetValue(CitizenKeys.FRIENDS, Collections.emptySet()).asImmutable();
        }
        return Sponge.getRegistry().getValueFactory().createSetValue(CitizenKeys.FRIENDS, this.friends).asImmutable();
    }

    @Override
    public ImmutableValue<Instant> joined() {
        return Sponge.getRegistry().getValueFactory().createValue(CitizenKeys.JOINED, this.joined).asImmutable();
    }

    private int getTown() {
        return this.town;
    }

    private Rank getRank() {
        return this.rank;
    }

    @Nullable
    private Set<UUID> getFriends() {
        return this.friends;
    }

    private Instant getJoined() {
        return this.joined;
    }
}
