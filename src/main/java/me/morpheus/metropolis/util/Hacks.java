package me.morpheus.metropolis.util;

import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteMaps;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import me.morpheus.metropolis.api.data.town.TownKeys;
import me.morpheus.metropolis.api.flag.Flag;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Hacks {

    private static final Method DESERIALIZE;
    private static final Method SERIALIZE;
    public static final DataQuery DATA_MANIPULATORS;
    private static final Field REQUIRED_CLASS;

    static {
        try {
            final Class<?> datautil = Class.forName("org.spongepowered.common.data.util.DataUtil");
            DATA_MANIPULATORS = (DataQuery) Class.forName("org.spongepowered.common.util.Constants$Sponge").getField("DATA_MANIPULATORS").get(null);
            DESERIALIZE = datautil.getMethod("deserializeManipulatorList", List.class);
            SERIALIZE = datautil.getMethod("getSerializedManipulatorList", Iterable.class);
            REQUIRED_CLASS = AbstractDataBuilder.class.getDeclaredField("requiredClass");
            REQUIRED_CLASS.setAccessible(true);
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("REPORT THIS", e);
        }
    }

    public static List<DataManipulator<?, ?>> deserializeManipulatorList(final List<? extends DataView> containers) {
        try {
            final Object serializedDataTransaction = DESERIALIZE.invoke(null, containers);
            return (List<DataManipulator<?, ?>>) serializedDataTransaction.getClass().getField("deserializedManipulators").get(serializedDataTransaction);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            throw new RuntimeException("REPORT THIS", e);
        }
    }

    public static List<DataView> serializeManipulatorList(final Iterable<? extends DataManipulator<?, ?>> manipulators) {
        try {
            return (List<DataView>) SERIALIZE.invoke(null, manipulators);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("REPORT THIS", e);
        }
    }

    public static Class getRequiredClass(DataManipulatorBuilder builder) {
        try {
            return (Class) REQUIRED_CLASS.get(builder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("REPORT THIS", e);
        }
    }

    public static DataContainer toContainer(Map<String, Location<World>> outposts) {
        final DataContainer view = DataContainer.createNew();
        for (Map.Entry<String, Location<World>> e : outposts.entrySet()) {
            view.set(DataQuery.of(e.getKey()), e.getValue());
        }
        return view;
    }

    public static Map<String, Location<World>> outpostFrom(DataContainer container) {
        Optional<? extends Map<?, ?>> oupostsOpt = container.getMap(TownKeys.OUTPOSTS.getQuery());
        if (!oupostsOpt.isPresent()) {
            return Collections.emptyMap();
        }
        Map<String, Location<World>> map = new HashMap<>();
        for (Map.Entry<?, ?> entry : oupostsOpt.get().entrySet()) {
            String key = (String) entry.getKey();
            Location loc = container.getView(TownKeys.OUTPOSTS.getQuery()).get().getSerializable(DataQuery.of(key), Location.class).get();
            map.put(key, loc);
        }
        return map;
    }

    public static DataContainer toContainer(Reference2ByteMap<Flag> permissions) {
        final DataContainer view = DataContainer.createNew();
        for (Reference2ByteMap.Entry<Flag> e : permissions.reference2ByteEntrySet()) {
            view.set(DataQuery.of(e.getKey().getId()), e.getByteValue());
        }
        return view;
    }

    public static Reference2ByteMap<Flag> permissionsFrom(DataContainer container) {
        Optional<? extends Map<?, ?>> permissionsOpt = container.getMap(DataQuery.of("permissions"));
        if (!permissionsOpt.isPresent()) {
            return Reference2ByteMaps.emptyMap();
        }
        Reference2ByteMap<Flag> permissions = new Reference2ByteOpenHashMap<>(permissionsOpt.get().size());
        for (Map.Entry<?, ?> entry : permissionsOpt.get().entrySet()) {
            String key = (String) entry.getKey();
            Flag flag = Sponge.getRegistry().getType(Flag.class, key).get();
            byte value = ((Integer) entry.getValue()).byteValue();
            permissions.put(flag, value);
        }
        return permissions;
    }

    private Hacks() {}
}
