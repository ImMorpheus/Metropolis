package me.morpheus.metropolis.event.town;

import me.morpheus.metropolis.api.event.town.UpgradeTownEvent;
import me.morpheus.metropolis.api.town.Town;
import me.morpheus.metropolis.api.town.Upgrade;
import org.spongepowered.api.event.cause.Cause;

public final class MPUpgradeTownEventPost implements UpgradeTownEvent.Post {

    private final Cause cause;
    private final Town town;
    private final Upgrade upgrade;

    public MPUpgradeTownEventPost(Cause cause, Town town, Upgrade upgrade) {
        this.cause = cause;
        this.town = town;
        this.upgrade = upgrade;
    }

    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public Town getTargetTown() {
        return this.town;
    }

    @Override
    public Upgrade getUpgrade() {
        return this.upgrade;
    }
}
