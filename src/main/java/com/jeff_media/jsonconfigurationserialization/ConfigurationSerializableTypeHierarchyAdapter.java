package com.jeff_media.jsonconfigurationserialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link JsonSerializer} and {@link JsonDeserializer} for {@link ConfigurationSerializable}s to be used with {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}
 */
public final class ConfigurationSerializableTypeHierarchyAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {

    private static final String SERIALIZED_TYPE_KEY = ConfigurationSerialization.SERIALIZED_TYPE_KEY;
    static final TypeToken<Map<String, Object>> MAP_TYPE = new TypeToken<Map<String, Object>>() {
    };

    static ConfigurationSerializable deserializeFromMap(Map<String, Object> map) throws IllegalArgumentException {
        deserializeInner(map);
        return ConfigurationSerialization.deserializeObject(map);
    }

    static Map<String, Object> serializeToMap(ConfigurationSerializable serializable) {
        Map<String, Object> map = new HashMap<>(serializable.serialize());
        map.put(SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
        serializeInner(map);
        return map;
    }

    private static void serializeInner(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof ConfigurationSerializable) {
                Map<String, Object> innerMap = new HashMap<>(((ConfigurationSerializable) entry.getValue()).serialize());
                innerMap.put(SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias((Class<? extends ConfigurationSerializable>) entry.getValue().getClass()));
                serializeInner(innerMap);
                entry.setValue(innerMap);
            }
        }
    }

    private static void deserializeInner(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            Object raw = entry.getValue();

            if (raw instanceof Map) {
                Map<String, Object> innerMap = (Map<String, Object>) raw;
                deserializeInner(innerMap);
                if (innerMap.containsKey(SERIALIZED_TYPE_KEY)) {
                    String alias = (String) innerMap.get(SERIALIZED_TYPE_KEY);
                    Class<? extends ConfigurationSerializable> clazz = ConfigurationSerialization.getClassByAlias(alias);
                    if (clazz != null) {
                        ConfigurationSerializable serializable = ConfigurationSerialization.deserializeObject(innerMap, clazz);
                        entry.setValue(serializable);
                    } else {
                        throw new IllegalArgumentException("Could not find class by alias: " + alias);
                    }
                }
            } else {
                // Gson is pretty stupid and deserializes all numbers as doubles, so we need to convert them back to ints if possible.
                // Otherwise, certain deserialization methods will not work, for example CraftMetaItem#buildEnchantments does an instanceof Integer check.
                // And no, we cannot use a custom ToNumberStrategy because Gson is stupid and does not allow us to specify a ToNumberStrategy for a specific type.
                if(raw instanceof Number) {
                    Number number = (Number) raw;
                    entry.setValue(narrowNumberType(number));
                }
            }
        }
    }


    private static Number narrowNumberType(Number number) {
        double asDouble = number.doubleValue();
        int asInt = number.intValue();
        long asLong = number.longValue();

        double longAsDouble = asLong;

        if(asDouble == longAsDouble) {
            if(asLong > Integer.MAX_VALUE) {
                return asLong;
            } else {
                return asInt;
            }
        }

        return asDouble;
    }

    @Override
    public JsonElement serialize(ConfigurationSerializable configurationSerializable, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(serializeToMap(configurationSerializable), MAP_TYPE.getType());
    }

    @Override
    public ConfigurationSerializable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return deserializeFromMap(jsonDeserializationContext.deserialize(jsonElement, MAP_TYPE.getType()));
    }
}
