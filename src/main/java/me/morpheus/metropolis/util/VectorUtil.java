package me.morpheus.metropolis.util;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class VectorUtil {

    public static Vector2i toChunk2i(final Location<World> loc) {
        final Vector3i cp = loc.getChunkPosition();
        return Vector2i.from(cp.getX(), cp.getZ());
    }

    private VectorUtil() {}
}
