package me.morpheus.metropolis.api.command.args;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

class PositiveBigDecimal extends CommandElement {

    PositiveBigDecimal(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        final String next = args.next();
        try {
            final BigDecimal amount = new BigDecimal(next);
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw args.createError(Text.of("Expected a positive amount, but input ", amount, " was not"));
            }
            return amount;
        } catch (NumberFormatException ex) {
            throw args.createError(Text.of("Expected a number, but input " + next + " was not"));
        }
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Collections.emptyList();
    }
}
