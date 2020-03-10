package me.morpheus.metropolis.config.category;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.morpheus.metropolis.api.config.PlotCategory;
import me.morpheus.metropolis.api.flag.Flag;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;

@ConfigSerializable
public class SimplePlotCategory implements PlotCategory {

    @Setting private Object2IntOpenHashMap<Flag> unowned = new Object2IntOpenHashMap<>();

    public SimplePlotCategory() {
        for (Flag flag : Sponge.getRegistry().getAllOf(Flag.class)) {
            this.unowned.put(flag, 0);
        }
    }

    @Override
    public int getUnownedPermission(Flag flag) {
        return this.unowned.getInt(flag);
    }

    public Object2IntMap<Flag> getUnowned() {
        return this.unowned;
    }
}
