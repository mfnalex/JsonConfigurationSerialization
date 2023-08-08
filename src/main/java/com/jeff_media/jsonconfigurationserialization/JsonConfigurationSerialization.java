package com.jeff_media.jsonconfigurationserialization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public final class JsonConfigurationSerialization {

    private static final Gson GSON = new Gson();
    private static final TypeToken<Map<String,Object>> MAP_TYPE = new TypeToken<Map<String,Object>>() {};

    private JsonConfigurationSerialization() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Serializes a ConfigurationSerializable to a Json String
     * @param serializable ConfigurationSerializable to serialize
     * @return Json String
     */
    public static Map<String,Object> serialize(ConfigurationSerializable serializable) {
        Map<String,Object> map = new HashMap<>(serializable.serialize());
        map.put("==", ConfigurationSerialization.getAlias(serializable.getClass()));
        serializeInner(map);
        return map;
    }

    private static void serializeInner(Map<String,Object> map) {
        for(Map.Entry<String,Object> entry : map.entrySet()) {
            if(entry.getValue() instanceof ConfigurationSerializable) {
                Map<String,Object> innerMap = new HashMap<>(((ConfigurationSerializable) entry.getValue()).serialize());
                innerMap.put("==", ConfigurationSerialization.getAlias((Class<? extends ConfigurationSerializable>) entry.getValue().getClass()));
                serializeInner(innerMap);
                entry.setValue(innerMap);
            }
        }
    }

    /**
     * Deserializes a ConfigurationSerializable from a Json String
     * @param json Json String
     * @return ConfigurationSerializable
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

    private static void deserializeInner(Map<String, Object> map) {
        for(Map.Entry<String,Object> entry : map.entrySet()) {
            if(entry.getValue() instanceof Map) {
                Map<String,Object> innerMap = (Map<String, Object>) entry.getValue();
                if(innerMap.containsKey("==")) {
                    String alias = (String) innerMap.get("==");
                    Class<? extends ConfigurationSerializable> clazz = ConfigurationSerialization.getClassByAlias(alias);
                    if(clazz != null) {
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
