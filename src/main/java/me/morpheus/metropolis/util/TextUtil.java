package me.morpheus.metropolis.util;

import me.morpheus.metropolis.Metropolis;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public final class TextUtil {

    private static final Text PLUGIN = Text.of(TextColors.GOLD, "[MP] ");

    public static Text watermark() {
        return PLUGIN;
    }

    public static Text watermark(Object object) {
        return PLUGIN.concat(Text.of(object));
    }

    public static Text watermark(Object... objects) {
        return PLUGIN.concat(Text.of(objects));
    }

    public static Text reset(Object... objects) {
        return Text.of(TextColors.RESET, TextStyles.RESET, Text.of(objects));
    }

    private TextUtil() {}
}
