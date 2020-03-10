package me.morpheus.metropolis.api.config;

import java.time.Duration;

public interface GlobalConfig {

    TownCategory getTownCategory();

    ChatCategory getChatCategory();

    EconomyCategory getEconomyCategory();

    boolean isPlotMessageEnabled();

}
