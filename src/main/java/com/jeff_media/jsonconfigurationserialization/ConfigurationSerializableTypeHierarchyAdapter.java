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

    static final TypeToken<Map<String, Object>> MAP_TYPE = new TypeToken<Map<String, Object>>() {
    };

    static ConfigurationSerializable deserializeFromMap(Map<String, Object> map) throws IllegalArgumentException {
        deserializeInner(map);
        return ConfigurationSerialization.deserializeObject(map);
    }

    @Override
    public JsonElement serialize(ConfigurationSerializable configurationSerializable, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(serializeToMap(configurationSerializable), MAP_TYPE.getType());
    }

    @Override
    public ConfigurationSerializable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return deserializeFromMap(jsonDeserializationContext.deserialize(jsonElement, MAP_TYPE.getType()));
    }

    static Map<String, Object> serializeToMap(ConfigurationSerializable serializable) {
        Map<String, Object> map = new HashMap<>(serializable.serialize());
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
        serializeInner(map);
        return map;
    }

    private static void serializeInner(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof ConfigurationSerializable) {
                Map<String, Object> innerMap = new HashMap<>(((ConfigurationSerializable) entry.getValue()).serialize());
                innerMap.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias((Class<? extends ConfigurationSerializable>) entry.getValue().getClass()));
                serializeInner(innerMap);
                entry.setValue(innerMap);
            }
        }
    }

    private static void deserializeInner(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> innerMap = (Map<String, Object>) entry.getValue();
                if (innerMap.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                    String alias = (String) innerMap.get("==");
                    Class<? extends ConfigurationSerializable> clazz = ConfigurationSerialization.getClassByAlias(alias);
                    if (clazz != null) {
                        deserializeInner(innerMap);
                        ConfigurationSerializable serializable = ConfigurationSerialization.deserializeObject(innerMap, clazz);
                        entry.setValue(serializable);
                    } else {
                        throw new IllegalArgumentException("Could not find class by alias: " + alias);
                    }
                }
            }
        }
    }
}
