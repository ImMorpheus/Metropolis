package me.morpheus.metropolis.data.town.outpost;

import me.morpheus.metropolis.api.data.town.TownKeys;
import me.morpheus.metropolis.api.data.town.outpost.ImmutableOutpostData;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
import me.morpheus.metropolis.data.DataVersions;
import me.morpheus.metropolis.util.Hacks;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MPOutpostData extends AbstractData<OutpostData, ImmutableOutpostData> implements OutpostData {

    @Nullable private Map<String, Location<World>> outposts;

    MPOutpostData() {
        this(null);
    }

    MPOutpostData(@Nullable Map<String, Location<World>> outposts) {
        this.outposts = outposts;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(TownKeys.OUTPOSTS, this::outposts);

        registerFieldGetter(TownKeys.OUTPOSTS, this::getOutposts);

        // Only on mutable implementation
        registerFieldSetter(TownKeys.OUTPOSTS, this::setOutposts);
    }

    @Override
    public Optional<OutpostData> fill(DataHolder dataHolder, MergeFunction overlap) {
        OutpostData merged = overlap.merge(this, dataHolder.get(OutpostData.class).orElse(null));
        this.outposts = merged.outposts().get();

        return Optional.of(this);
    }

    @Override
    public Optional<OutpostData> from(DataContainer container) {
        final Map<String, Location<World>> map = Hacks.outpostFrom(container);
        if (map.isEmpty()) {
            return Optional.empty();
        }
        this.outposts = map;
        return Optional.of(this);
    }

    @Override
    public OutpostData copy() {
        return new MPOutpostData(this.outposts);
    }

    @Override
    public ImmutableOutpostData asImmutable() {
        return new ImmutableMPOutpostData(this.outposts);
    }

    @Override
    public int getContentVersion() {
        return DataVersions.OutpostData.CURRENT_VERSION;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer container = super.toContainer();
        if (this.outposts != null && !this.outposts.isEmpty()) {
            container.set(TownKeys.OUTPOSTS.getQuery(), this.outposts);
        }

        return container;
    }

    @Override
    public MapValue<String, Location<World>> outposts() {
        if (this.outposts == null) {
            this.outposts = new HashMap<>();
        }
        return Sponge.getRegistry().getValueFactory().createMapValue(TownKeys.OUTPOSTS, this.outposts);
    }

    @Nullable
    private Map<String, Location<World>> getOutposts() {
        return this.outposts;
    }

    private void setOutposts(@Nullable Map<String, Location<World>> outposts) {
        this.outposts = outposts;
    }
}
