package me.morpheus.metropolis.api.config;

import java.time.Duration;

public interface TownCategory {

    PlotCategory getPlotCategory();

    DefaultsCategory getDefaultsCategory();

    byte getTagMinLength();

    byte getTagMaxLength();

    byte getNameMinLength();

    byte getNameMaxLength();

    Duration getFoundationGracePeriod();

    Duration getKickForInactivity();

    Duration getInvitationDuration();

}
