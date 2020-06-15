package me.morpheus.metropolis.api.town.invitation;

import me.morpheus.metropolis.api.town.Town;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface Invitation {

    Optional<UUID> getSource();

    UUID getTarget();

    Optional<Town> getTown();

    Instant getInstant();

    boolean isValid();

    boolean accept();

    boolean refuse();

}
