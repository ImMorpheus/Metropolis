package me.morpheus.metropolis.flag;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.morpheus.metropolis.api.flag.Flag;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
class MPFlag implements Flag {

    @Setting private String id;
    @Setting private String name;

    MPFlag(String id, String name) {
        this.id = id;
        this.name = name;
    }

    private MPFlag() {
        this.id = "dummy";
        this.name = "DUMMY";
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
