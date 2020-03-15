package me.morpheus.metropolis.api.data.plot;

import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.util.MPTypeTokens;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TypeTokens;

import java.util.UUID;

public final class PlotKeys {

    public static final Key<Value<Integer>> TOWN = Key.builder()
            .id("town")
            .name("Town")
            .query(DataQuery.of("Town"))
            .type(TypeTokens.INTEGER_VALUE_TOKEN)
            .build();

    public static final Key<Value<Text>> NAME = Key.builder()
            .id("name")
            .name("name")
            .query(DataQuery.of("Name"))
            .type(TypeTokens.TEXT_VALUE_TOKEN)
            .build();

    public static final Key<OptionalValue<UUID>> OWNER = Key.builder()
            .id("owner")
            .name("Owner")
            .query(DataQuery.of("Owner"))
            .type(TypeTokens.OPTIONAL_UUID_VALUE_TOKEN)
            .build();

    public static final Key<Value<Double>> PRICE = Key.builder()
            .id("price")
            .name("Price")
            .query(DataQuery.of("Price"))
            .type(TypeTokens.DOUBLE_VALUE_TOKEN)
            .build();

    public static final Key<Value<Double>> RENT = Key.builder()
            .id("rent")
            .name("Rent")
            .query(DataQuery.of("Rent"))
            .type(TypeTokens.DOUBLE_VALUE_TOKEN)
            .build();

    public static final Key<Value<Boolean>> FORSALE = Key.builder()
            .id("for_sale")
            .name("ForSale")
            .query(DataQuery.of("ForSale"))
            .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
            .build();

    public static final Key<Value<PlotType>> TYPE = Key.builder()
            .id("type")
            .name("Type")
            .query(DataQuery.of("type"))
            .type(MPTypeTokens.PLOTTYPE_VALUE_TOKEN)
            .build();

    public static final Key<Value<Boolean>> MOBSPAWN = Key.builder()
            .id("mobspawn")
            .name("MobSpawn")
            .query(DataQuery.of("mobspawn"))
            .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
            .build();

    private PlotKeys() {}
}
