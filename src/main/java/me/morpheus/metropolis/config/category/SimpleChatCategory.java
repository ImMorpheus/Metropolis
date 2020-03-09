package me.morpheus.metropolis.config.category;

import me.morpheus.metropolis.api.config.ChatCategory;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

@ConfigSerializable
public class SimpleChatCategory implements ChatCategory {

    @Setting(comment = "Enable chat integration")
    private boolean enabled = true;

    @Setting(comment = "")
    private TextTemplate prefix = TextTemplate.of(
            TextColors.AQUA,
            "[", TextTemplate.arg("tag"), "] ",
            "[", TextTemplate.arg("rank"), "] "
    );

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public TextTemplate getPrefix() {
        return this.prefix;
    }
}
