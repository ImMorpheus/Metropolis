package me.morpheus.metropolis.event.item.inventory;

import com.flowpowered.math.vector.Vector3d;
import me.morpheus.metropolis.api.event.block.InteractBlockTownEvent;
import me.morpheus.metropolis.api.event.item.inventory.InteractItemTownEvent;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nullable;
import java.util.Optional;

public final class MPInteractItemTownEventPrimary implements InteractItemTownEvent.Primary {

    private final Cause cause;
    @Nullable private final Vector3d hit;
    private final ItemStackSnapshot itemStack;
    private boolean cancelled = false;

    public MPInteractItemTownEventPrimary(Cause cause, @Nullable Vector3d hit, ItemStackSnapshot itemStack) {
        this.cause = cause;
        this.hit = hit;
        this.itemStack = itemStack;
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
    public ItemStackSnapshot getItemStack() {
        return this.itemStack;
    }
}
