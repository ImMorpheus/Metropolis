package me.morpheus.metropolis.api.util;

import com.google.common.reflect.TypeToken;
import me.morpheus.metropolis.api.plot.PlotType;
import me.morpheus.metropolis.api.rank.Rank;
import org.spongepowered.api.data.value.mutable.MapValue;
import org.spongepowered.api.data.value.mutable.SetValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Duration;
import java.util.UUID;

public class MPTypeTokens {

    public static final TypeToken<Value<Rank>> RANK_VALUE_TOKEN = new TypeToken<Value<Rank>>() { private static final long serialVersionUID = -1; };

    public static final TypeToken<SetValue<UUID>> SET_UUID_VALUE_TOKEN = new TypeToken<SetValue<UUID>>() { private static final long serialVersionUID = -1; };

    public static final TypeToken<Value<PlotType>> PLOTTYPE_VALUE_TOKEN = new TypeToken<Value<PlotType>>() { private static final long serialVersionUID = -1; };

    public static final TypeToken<MapValue<String, Location<World>>> STRING_LOCATION_MAP_VALUE_TOKEN = new TypeToken<MapValue<String, Location<World>>>() { private static final long serialVersionUID = -1; };

    public static final TypeToken<Duration> DURATION_TOKEN = TypeToken.of(Duration.class);

}
