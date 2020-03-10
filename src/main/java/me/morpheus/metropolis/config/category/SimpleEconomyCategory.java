package me.morpheus.metropolis.config.category;

import me.morpheus.metropolis.api.config.EconomyCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SimpleEconomyCategory implements EconomyCategory {

    @Setting(comment = "Enable economy integration")
    private boolean enabled = true;

    @Setting(value = "town-creation", comment = "")
    private double creation = 100.0;

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public double getTownCreationPrice() {
        return this.creation;
    }
}
