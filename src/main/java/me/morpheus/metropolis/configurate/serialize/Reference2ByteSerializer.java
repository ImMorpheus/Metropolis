package me.morpheus.metropolis.configurate.serialize;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

public class Reference2ByteSerializer implements TypeSerializer<Reference2ByteMap> {

    @Override
    public Reference2ByteMap deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Reference2ByteMap map = new Reference2ByteOpenHashMap();
        if (!value.hasMapChildren()) {
            return map;
        }

        if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }

        final TypeToken<?> key = type.resolveType(Reference2ByteMap.class.getTypeParameters()[0]);
        final TypeSerializer<?> keySerial = value.getOptions().getSerializers().get(key);

        if (keySerial == null) {
            throw new ObjectMappingException("No type serializer available for type " + key);
        }

        for (Map.Entry<Object, ? extends ConfigurationNode> ent : value.getChildrenMap().entrySet()) {
            final Object keyValue = keySerial.deserialize(key, SimpleConfigurationNode.root().setValue(ent.getKey()));
            final byte valueValue = (byte) ent.getValue().getInt();
            if (keyValue != null) {
                map.put(keyValue, valueValue);
            }
        }

        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, Reference2ByteMap obj, ConfigurationNode value) throws ObjectMappingException {
        if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }

        final TypeToken<?> key = type.resolveType(Reference2ByteMap.class.getTypeParameters()[0]);
        final TypeSerializer keySerial = value.getOptions().getSerializers().get(key);

        if (keySerial == null) {
            throw new ObjectMappingException("No type serializer available for type " + key);
        }

        value.setValue(ImmutableMap.of());

        for (Object o : obj.reference2ByteEntrySet()) {
            Reference2ByteMap.Entry ent = (Reference2ByteMap.Entry) o;
            SimpleConfigurationNode keyNode = SimpleConfigurationNode.root();
            keySerial.serialize(key, ent.getKey(), keyNode);
            value.getNode(keyNode.getValue()).setValue(ent.getByteValue());
        }
    }

}


