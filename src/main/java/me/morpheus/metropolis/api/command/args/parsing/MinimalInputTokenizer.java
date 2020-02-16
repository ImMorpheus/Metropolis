package me.morpheus.metropolis.api.command.args.parsing;

import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.args.parsing.SingleArg;

import java.util.Collections;
import java.util.List;

public class MinimalInputTokenizer implements InputTokenizer {
    public static final MinimalInputTokenizer INSTANCE = new MinimalInputTokenizer();

    private MinimalInputTokenizer() {}

    @Override
    public List<SingleArg> tokenize(String arguments, boolean lenient) throws ArgumentParseException {
        if (arguments.isEmpty()) {
            return Collections.emptyList();
        }
        return InputTokenizer.rawInput().tokenize(arguments, lenient);
    }
}
