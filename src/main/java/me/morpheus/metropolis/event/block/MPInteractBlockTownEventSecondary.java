package me.morpheus.metropolis.event.block;

import com.flowpowered.math.vector.Vector3d;
import me.morpheus.metropolis.api.event.block.InteractBlockTownEvent;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.cause.Cause;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MPInteractBlockTownEventSecondary implements InteractBlockTownEvent.Secondary {

    private final Cause cause;
    private final Vector3d hit;
    private final BlockSnapshot target;
    private boolean cancelled = false;

    public MPInteractBlockTownEventSecondary(Cause cause, @Nullable Vector3d hit, BlockSnapshot target) {
        this.cause = cause;
        this.hit = hit;
        this.target = target;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Optional<Vector3d> getInteractionPoint() {
        return Optional.ofNullable(this.hit);
    }

    @Override
    public BlockSnapshot getTargetBlock() {
        return this.target;
    }
}
