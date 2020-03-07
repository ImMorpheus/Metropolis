package me.morpheus.metropolis.api.event.town;

import me.morpheus.metropolis.api.town.Upgrade;
import org.spongepowered.api.event.Cancellable;

public interface UpgradeTownEvent extends TargetTownEvent {

    Upgrade getUpgrade();

    interface Pre extends UpgradeTownEvent, Cancellable {}

    interface Post extends UpgradeTownEvent {}
}
