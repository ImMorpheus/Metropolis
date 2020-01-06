package me.morpheus.metropolis.error;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public final class MPGenericErrors {

    public static Text config() {
        return Text.builder()
                .append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "+----------------------------+")).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "|        CONFIG ERROR        |")).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "+----------------------------+")).append(Text.NEW_LINE)
                .append(Text.EMPTY).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "The server has been whitelisted to protect the towns")).append(Text.NEW_LINE)
                .build();
    }

    public static Text economyService() {
        return Text.builder()
                .append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "+----------------------------+")).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "| MISSING ECONOMYSERVICE     |")).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "+----------------------------+")).append(Text.NEW_LINE)
                .append(Text.EMPTY).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "The server has been whitelisted to protect the towns")).append(Text.NEW_LINE)
                .build();
    }

    public static Text rank(String mayor, String citizen) {
        return Text.builder()
                .append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "+----------------------------+")).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "| MISSING DEFAULT RANKS      |")).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "+----------------------------+")).append(Text.NEW_LINE)
                .append(Text.EMPTY).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "| mayor:   ", mayor)).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "| citizen: ", citizen)).append(Text.NEW_LINE)
                .append(Text.EMPTY).append(Text.NEW_LINE)
                .append(Text.of(TextColors.RED, "The server has been whitelisted to protect the towns")).append(Text.NEW_LINE)
                .build();
    }

    private MPGenericErrors() {}
}
