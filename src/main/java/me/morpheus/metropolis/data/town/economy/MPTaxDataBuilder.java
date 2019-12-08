package me.morpheus.metropolis.data.town.economy;

import me.morpheus.metropolis.api.data.town.economy.ImmutableTaxData;
import me.morpheus.metropolis.api.data.town.economy.TaxData;
import me.morpheus.metropolis.data.DataVersions;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class MPTaxDataBuilder extends AbstractDataBuilder<TaxData> implements DataManipulatorBuilder<TaxData, ImmutableTaxData> {

    public MPTaxDataBuilder() {
        super(TaxData.class, DataVersions.TaxData.CURRENT_VERSION);
    }

    @Override
    public TaxData create() {
        return new MPTaxData();
    }

    @Override
    public Optional<TaxData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<TaxData> buildContent(DataView container) throws InvalidDataException {
        return create().from(container.copy());
    }
}
