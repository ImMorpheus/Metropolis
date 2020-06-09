package me.morpheus.metropolis.data.citizen;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.CitizenKeys;
import me.morpheus.metropolis.api.data.citizen.ImmutableCitizenData;
import me.morpheus.metropolis.api.rank.Rank;
import me.morpheus.metropolis.api.rank.Ranks;
import me.morpheus.metropolis.data.DataVersions;
import me.morpheus.metropolis.util.Hacks;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class MPCitizenData extends AbstractData<CitizenData, ImmutableCitizenData> implements CitizenData {

    private int town;
    private Rank rank;
    @Nullable private Set<UUID> friends;
    private Instant joined;

    MPCitizenData() {
        this(Integer.MIN_VALUE, Ranks.CITIZEN, null, Instant.now());
    }

    MPCitizenData(int town, Rank rank, @Nullable Set<UUID> friends, Instant joined) {
        this.town = town;
        this.rank = rank;
        this.friends = friends;
        this.joined = joined;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(CitizenKeys.TOWN, this::town);
        registerKeyValue(CitizenKeys.RANK, this::rank);
        registerKeyValue(CitizenKeys.FRIENDS, this::friends);
        registerKeyValue(CitizenKeys.JOINED, this::joined);

        registerFieldGetter(CitizenKeys.TOWN, this::getTown);
        registerFieldGetter(CitizenKeys.RANK, this::getRank);
        registerFieldGetter(CitizenKeys.FRIENDS, this::getFriends);
        registerFieldGetter(CitizenKeys.JOINED, this::getJoined);

        // Only on mutable implementation
        registerFieldSetter(CitizenKeys.TOWN, this::setTown);
        registerFieldSetter(CitizenKeys.RANK, this::setRank);
        registerFieldSetter(CitizenKeys.FRIENDS, this::setFriends);
        registerFieldSetter(CitizenKeys.JOINED, this::setJoined);
    }

    @Override
    public Optional<CitizenData> fill(DataHolder dataHolder, MergeFunction overlap) {
        CitizenData merged = overlap.merge(this, dataHolder.get(CitizenData.class).orElse(null));
        this.town = merged.town().get();
        this.rank = merged.rank().get();
        this.friends = merged.friends().get();
        this.joined = merged.joined().get();

        return Optional.of(this);
    }

    @Override
    public Optional<CitizenData> from(DataContainer container) {
        Optional<Integer> town = container.getInt(CitizenKeys.TOWN.getQuery());
        if (!town.isPresent()) {
            return Optional.empty();
        }
        Optional<Rank> rank = container.getCatalogType(CitizenKeys.RANK.getQuery(), Rank.class);
        if (!rank.isPresent()) {
            return Optional.empty();
        }
        Set<UUID> friends = Hacks.friendsFrom(container);
        Optional<Instant> joined = container.getObject(CitizenKeys.JOINED.getQuery(), Instant.class);

        this.town = town.get();
        this.rank = rank.get();
        if (!friends.isEmpty()) {
            this.friends = friends;
        }
        this.joined = joined.orElse(Instant.now());
        return Optional.of(this);
    }

    @Override
    public CitizenData copy() {
        return new MPCitizenData(this.town, this.rank, this.friends, this.joined);
    }

    @Override
    public ImmutableCitizenData asImmutable() {
        return new ImmutableMPCitizenData(this.town, this.rank, this.friends, this.joined);
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
    public Value<Integer> town() {
        return Sponge.getRegistry().getValueFactory().createValue(CitizenKeys.TOWN, this.town);
    }

    @Override
    public Value<Rank> rank() {
        return Sponge.getRegistry().getValueFactory().createValue(CitizenKeys.RANK, this.rank);
    }

    @Override
    public SetValue<UUID> friends() {
        if (this.friends == null) {
            this.friends = new HashSet<>();
        }
        return Sponge.getRegistry().getValueFactory().createSetValue(CitizenKeys.FRIENDS, this.friends);
    }

    @Override
    public Value<Instant> joined() {
        return Sponge.getRegistry().getValueFactory().createValue(CitizenKeys.JOINED, this.joined);
    }

    private int getTown() {
        return this.town;
    }

    private void setTown(int town) {
        this.town = town;
    }

    private Rank getRank() {
        return this.rank;
    }

    private void setRank(Rank rank) {
        this.rank = rank;
    }

    @Nullable
    private Set<UUID> getFriends() {
        return this.friends;
    }

    private void setFriends(@Nullable Set<UUID> friends) {
        this.friends = friends;
    }

    private Instant getJoined() {
        return this.joined;
    }

    private void setJoined(Instant joined) {
        this.joined = joined;
    }
}
