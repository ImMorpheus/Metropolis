package me.morpheus.metropolis.data.citizen;

import me.morpheus.metropolis.api.data.citizen.CitizenData;
import me.morpheus.metropolis.api.data.citizen.ImmutableCitizenData;
import me.morpheus.metropolis.data.DataVersions;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class MPCitizenDataBuilder extends AbstractDataBuilder<CitizenData> implements DataManipulatorBuilder<CitizenData, ImmutableCitizenData> {

    public MPCitizenDataBuilder() {
        super(CitizenData.class, DataVersions.CitizenData.CURRENT_VERSION);
    }

    @Override
    public CitizenData create() {
        return new MPCitizenData();
    }

    @Override
    public Optional<CitizenData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<CitizenData> buildContent(DataView container) throws InvalidDataException {
        return create().from(container.copy());
    }
}
