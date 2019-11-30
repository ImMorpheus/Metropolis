package me.morpheus.metropolis.config.category;

import me.morpheus.metropolis.api.config.DefaultsCategory;
import me.morpheus.metropolis.api.config.PlotCategory;
import me.morpheus.metropolis.api.config.TownCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.time.Duration;

@ConfigSerializable
public class SimpleTownCategory implements TownCategory {

    @Setting(comment = "+--------------- Plot ") private SimplePlotCategory plot = new SimplePlotCategory();
    @Setting(comment = "+--------------- Defaults ") private SimpleDefaultsCategory defaults = new SimpleDefaultsCategory();
    @Setting(value = "tag-min-length") private int tagMinLength = 3;
    @Setting(value = "tag-max-length") private int tagMaxLength = 3;
    @Setting(value = "name-min-length") private int nameMinLength = 3;
    @Setting(value = "name-max-length") private int nameMaxLength = 20;
    @Setting(value = "foundation-grace-period") private Duration foundationGracePeriod = Duration.ofDays(3);
    @Setting(value = "inactive-days") private Duration inactiveDays = Duration.ofDays(30);
    @Setting(value = "invitation-duration") private Duration invitationDuration = Duration.ofMinutes(5);

    @Override
    public PlotCategory getPlotCategory() {
        return this.plot;
    }

    @Override
    public DefaultsCategory getDefaultsCategory() {
        return this.defaults;
    }

    @Override
    public int getTagMinLength() {
        return this.tagMinLength;
    }

    @Override
    public int getTagMaxLength() {
        return this.tagMaxLength;
    }

    @Override
    public int getNameMinLength() {
        return this.nameMinLength;
    }

    @Override
    public int getNameMaxLength() {
        return this.nameMaxLength;
    }

    @Override
    public Duration getFoundationGracePeriod() {
        return this.foundationGracePeriod;
    }

    @Override
    public Duration getInactiveDays() {
        return this.inactiveDays;
    }

    @Override
    public Duration getInvitationDuration() {
        return this.invitationDuration;
    }
}
