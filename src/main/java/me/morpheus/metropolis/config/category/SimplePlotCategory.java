package me.morpheus.metropolis.config.category;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import me.morpheus.metropolis.api.config.PlotCategory;
import me.morpheus.metropolis.api.flag.Flag;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;

@ConfigSerializable
public class SimplePlotCategory implements PlotCategory {

    @Setting private Reference2ByteMap<Flag> unowned = new Reference2ByteOpenHashMap<>();

    public SimplePlotCategory() {
        for (Flag flag : Sponge.getRegistry().getAllOf(Flag.class)) {
            this.unowned.put(flag, (byte) 0);
        }
    }

    @Override
    public byte getUnownedPermission(Flag flag) {
        return this.unowned.getByte(flag);
    }

    public Reference2ByteMap<Flag> getUnowned() {
        return this.unowned;
    }
}
