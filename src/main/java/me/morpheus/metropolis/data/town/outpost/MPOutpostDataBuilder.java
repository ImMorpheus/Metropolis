package me.morpheus.metropolis.data.town.outpost;

import me.morpheus.metropolis.api.data.town.outpost.ImmutableOutpostData;
import me.morpheus.metropolis.api.data.town.outpost.OutpostData;
import me.morpheus.metropolis.data.DataVersions;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class MPOutpostDataBuilder extends AbstractDataBuilder<OutpostData> implements DataManipulatorBuilder<OutpostData, ImmutableOutpostData> {

    public MPOutpostDataBuilder() {
        super(OutpostData.class, DataVersions.OutpostData.CURRENT_VERSION);
    }

    @Override
    public OutpostData create() {
        return new MPOutpostData();
    }

    @Override
    public Optional<OutpostData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<OutpostData> buildContent(DataView container) throws InvalidDataException {
        return create().from(container.copy());
    }
}
