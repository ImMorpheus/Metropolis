package me.morpheus.metropolis.util;

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
import java.util.List;

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

    public static DataContainer toContainer(Location<World> location) {
        return DataContainer.createNew()
                .set(Queries.CONTENT_VERSION, 1)
                .set(Queries.WORLD_NAME, location.getExtent().getName())
                .set(Queries.WORLD_ID, location.getExtent().getUniqueId().toString())
                .set(Queries.POSITION_X, location.getX())
                .set(Queries.POSITION_Y, location.getY())
                .set(Queries.POSITION_Z, location.getZ());
    }

    private Hacks() {}
}
