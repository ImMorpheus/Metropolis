package me.morpheus.metropolis.data.town;

import me.morpheus.metropolis.api.data.town.ImmutableTownData;
import me.morpheus.metropolis.api.data.town.TownData;
import me.morpheus.metropolis.data.DataVersions;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class MPTownDataBuilder extends AbstractDataBuilder<TownData> implements DataManipulatorBuilder<TownData, ImmutableTownData> {

    public MPTownDataBuilder() {
        super(TownData.class, DataVersions.TownData.CURRENT_VERSION);
    }

    @Override
    public TownData create() {
        return new MPTownData();
    }

    @Override
    public Optional<TownData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<TownData> buildContent(DataView container) throws InvalidDataException {
        return create().from(container.copy());
    }
}
