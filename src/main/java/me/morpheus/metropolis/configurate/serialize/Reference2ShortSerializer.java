package me.morpheus.metropolis.configurate.serialize;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Reference2ShortMap;
import it.unimi.dsi.fastutil.objects.Reference2ShortOpenHashMap;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

public class Reference2ShortSerializer implements TypeSerializer<Reference2ShortMap> {

    @Override
    public Reference2ShortMap deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Reference2ShortMap map = new Reference2ShortOpenHashMap();
        if (!value.hasMapChildren()) {
            return map;
        }

        if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }

        final TypeToken<?> key = type.resolveType(Reference2ShortMap.class.getTypeParameters()[0]);
        final TypeSerializer<?> keySerial = value.getOptions().getSerializers().get(key);

        if (keySerial == null) {
            throw new ObjectMappingException("No type serializer available for type " + key);
        }

        for (Map.Entry<Object, ? extends ConfigurationNode> ent : value.getChildrenMap().entrySet()) {
            final Object keyValue = keySerial.deserialize(key, SimpleConfigurationNode.root().setValue(ent.getKey()));
            final short valueValue = (short) ent.getValue().getInt();
            if (keyValue != null) {
                map.put(keyValue, valueValue);
            }
        }

        return map;
    }

    @Override
    public void serialize(TypeToken<?> type, Reference2ShortMap obj, ConfigurationNode value) throws ObjectMappingException {
        if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }

        final TypeToken<?> key = type.resolveType(Reference2ShortMap.class.getTypeParameters()[0]);
        final TypeSerializer keySerial = value.getOptions().getSerializers().get(key);

        if (keySerial == null) {
            throw new ObjectMappingException("No type serializer available for type " + key);
        }

        value.setValue(ImmutableMap.of());

        for (Object o : obj.reference2ShortEntrySet()) {
            Reference2ShortMap.Entry ent = (Reference2ShortMap.Entry) o;
            SimpleConfigurationNode keyNode = SimpleConfigurationNode.root();
            keySerial.serialize(key, ent.getKey(), keyNode);
            value.getNode(keyNode.getValue()).setValue(ent.getShortValue());
        }
    }

}


