package me.morpheus.metropolis.rank;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.morpheus.metropolis.api.flag.Flag;
import me.morpheus.metropolis.api.rank.Rank;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
class MPRank implements Rank {

    @Setting private String id;
    @Setting private String name;
    @Setting(value = "tax-exempt") private boolean isTaxExempt;
    @Setting(value = "can-be-kicked-for-inactivity") private boolean canBeKickedForInactivity;
    @Setting(value = "can-leave") private boolean canLeave;
    @Setting private Object2IntMap<Flag> permissions = new Object2IntOpenHashMap<>();

    MPRank(String id, String name, boolean isTaxExempt, boolean canBeKickedForInactivity, boolean canLeave, Object2IntMap<Flag> permissions) {
        this.id = id;
        this.name = name;
        this.isTaxExempt = isTaxExempt;
        this.canBeKickedForInactivity = canBeKickedForInactivity;
        this.canLeave = canLeave;
        this.permissions.putAll(permissions);
        this.permissions.defaultReturnValue(Integer.MIN_VALUE);
    }

    private MPRank() {
        this.id = "dummy";
        this.name = "DUMMY";
        this.isTaxExempt = true;
        this.canBeKickedForInactivity = false;
        this.canLeave = false;
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
    public boolean isTaxExempt() {
        return this.isTaxExempt;
    }

    @Override
    public boolean canBeKickedForInactivity() {
        return this.canBeKickedForInactivity;
    }

    @Override
    public boolean canLeave() {
        return this.canLeave;
    }

    @Override
    public int getPermission(Flag flag) {
        return this.permissions.getInt(flag);
    }

    public Object2IntMap<Flag> getPermissions() {
        return this.permissions;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Id", this.id)
                .add("Name", this.name)
                .toString();
    }
}
