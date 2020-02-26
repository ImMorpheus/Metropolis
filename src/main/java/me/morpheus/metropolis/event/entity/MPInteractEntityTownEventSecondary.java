package me.morpheus.metropolis.event.entity;

import com.flowpowered.math.vector.Vector3d;
import me.morpheus.metropolis.api.event.entity.InteractEntityTownEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MPInteractEntityTownEventSecondary implements InteractEntityTownEvent.Secondary {

    private final Cause cause;
    @Nullable private final Vector3d hit;
    private final Entity entity;
    private boolean cancelled = false;

    public MPInteractEntityTownEventSecondary(Cause cause, @Nullable Vector3d hit, Entity entity) {
        this.cause = cause;
        this.hit = hit;
        this.entity = entity;
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
    public Entity getTargetEntity() {
        return this.entity;
    }
}
