package com.jeff_media.jsonconfigurationserialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for serializing and deserializing ConfigurationSerializables to and from Json
 */
public final class JsonConfigurationSerialization {

    /**
     * A {@link JsonSerializer} and {@link JsonDeserializer} for {@link ConfigurationSerializable}s to be used with {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}
     */
    public static final ConfigurationSerializableTypeHierarchyAdapter TYPE_HIERARCHY_ADAPTER = new ConfigurationSerializableTypeHierarchyAdapter();
    private static final Gson GSON = new Gson();
    static final TypeToken<Map<String, Object>> MAP_TYPE = new TypeToken<Map<String, Object>>() {
    };

    private JsonConfigurationSerialization() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Serializes a ConfigurationSerializable to a Json String
     *
     * @param serializable ConfigurationSerializable to serialize
     * @return Json String
     */
    public static String serialize(ConfigurationSerializable serializable) {
        return GSON.toJson(serializeToMap(serializable), MAP_TYPE.getType());
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

    /**
     * Deserializes a ConfigurationSerializable from a Json String
     *
     * @param json Json String
     * @return deserialized ConfigurationSerializable
     * @throws IllegalArgumentException if the Json String is invalid or if the ConfigurationSerializable class is not found
     */
    public static ConfigurationSerializable deserialize(String json) throws IllegalArgumentException {
        try {
            Map<String, Object> map = GSON.fromJson(json, MAP_TYPE.getType());
            deserializeInner(map);
            return ConfigurationSerialization.deserializeObject(map);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    static ConfigurationSerializable deserializeFromMap(Map<String, Object> map) throws IllegalArgumentException {
        deserializeInner(map);
        return ConfigurationSerialization.deserializeObject(map);
    }

    /**
     * Deserializes a ConfigurationSerializable from a Json String
     *
     * @param json  Json String
     * @param clazz Class of the ConfigurationSerializable
     * @return deserialized ConfigurationSerializable
     * @throws IllegalArgumentException if the Json String is invalid or if the ConfigurationSerializable class is not found
     * @throws ClassCastException       if the ConfigurationSerializable is not of the specified class
     */
    public static <T extends ConfigurationSerializable> T deserialize(String json, Class<T> clazz) throws IllegalArgumentException, ClassCastException {
        return clazz.cast(deserialize(json));
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
