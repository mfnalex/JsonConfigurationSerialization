package com.jeff_media.jsonconfigurationserialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Utility class for serializing and deserializing ConfigurationSerializables to and from Json
 */
public final class JsonConfigurationSerialization {

    /**
     * A {@link JsonSerializer} and {@link JsonDeserializer} for {@link ConfigurationSerializable}s to be used with {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}
     */
    public static final ConfigurationSerializableTypeHierarchyAdapter TYPE_HIERARCHY_ADAPTER = new ConfigurationSerializableTypeHierarchyAdapter();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ConfigurationSerializable.class, TYPE_HIERARCHY_ADAPTER)
            .create();


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
        return GSON.toJson(serializable, ConfigurationSerializable.class);
    }


    /**
     * Deserializes a ConfigurationSerializable from a Json String
     *
     * @param json Json String
     * @return deserialized ConfigurationSerializable
     * @throws IllegalArgumentException if the Json String is invalid or if the ConfigurationSerializable class is not found
     */
    public static ConfigurationSerializable deserialize(String json) throws IllegalArgumentException {
        return GSON.fromJson(json, ConfigurationSerializable.class);
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
        return GSON.fromJson(json, clazz);
    }


}
