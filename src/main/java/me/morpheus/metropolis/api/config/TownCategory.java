package me.morpheus.metropolis.api.config;

import java.time.Duration;

public interface TownCategory {

    PlotCategory getPlotCategory();

    DefaultsCategory getDefaultsCategory();

    int getTagMinLength();

    int getTagMaxLength();

    int getNameMinLength();

    int getNameMaxLength();

    Duration getFoundationGracePeriod();

    Duration getKickForInactivity();

    Duration getInvitationDuration();

}
