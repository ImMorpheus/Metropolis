package me.morpheus.metropolis.config;

import me.morpheus.metropolis.api.config.ChatCategory;
import me.morpheus.metropolis.api.config.EconomyCategory;
import me.morpheus.metropolis.api.config.GlobalConfig;
import me.morpheus.metropolis.api.config.TownCategory;
import me.morpheus.metropolis.config.category.SimpleChatCategory;
import me.morpheus.metropolis.config.category.SimpleEconomyCategory;
import me.morpheus.metropolis.config.category.SimpleTownCategory;
import ninja.leaping.configurate.objectmapping.Setting;

import java.time.Duration;

public class Global implements GlobalConfig {

    @Setting(value = "town", comment =
            " +------------------------------------------------------+ \n" +
            " |                         Town                         | \n" +
            " +------------------------------------------------------+   "
    )
    private SimpleTownCategory town = new SimpleTownCategory();

    @Setting(comment =
            " +------------------------------------------------------+ \n" +
            " |                         Chat                         | \n" +
            " +------------------------------------------------------+   "
    )
    private SimpleChatCategory chat = new SimpleChatCategory();

    @Setting(comment =
            " +------------------------------------------------------+ \n" +
            " |                        Economy                       | \n" +
            " +------------------------------------------------------+   "
    )
    private SimpleEconomyCategory economy = new SimpleEconomyCategory();

    @Setting(value = "enable-plot-message", comment = "")
    private boolean plotMessage = true;

    @Override
    public TownCategory getTownCategory() {
        return this.town;
    }

    @Override
    public ChatCategory getChatCategory() {
        return this.chat;
    }

    @Override
    public EconomyCategory getEconomyCategory() {
        return this.economy;
    }

    @Override
    public boolean isPlotMessageEnabled() {
        return this.plotMessage;
    }
}
