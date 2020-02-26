package me.morpheus.metropolis.plot;

import com.google.common.base.MoreObjects;
import me.morpheus.metropolis.api.plot.PlotType;

class MPPlotType implements PlotType {

    private final String id;
    private final String name;

    MPPlotType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Id", this.id)
                .add("Name", this.name)
                .toString();
    }
}
