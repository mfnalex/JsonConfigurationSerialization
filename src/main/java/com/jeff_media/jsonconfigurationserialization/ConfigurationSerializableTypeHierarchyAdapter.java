package com.jeff_media.jsonconfigurationserialization;

import com.google.gson.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Type;

/**
 * A {@link JsonSerializer} and {@link JsonDeserializer} for {@link ConfigurationSerializable}s to be used with {@link GsonBuilder#registerTypeHierarchyAdapter(Class, Object)}
 */
public final class ConfigurationSerializableTypeHierarchyAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {

    @Override
    public JsonElement serialize(ConfigurationSerializable configurationSerializable, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(JsonConfigurationSerialization.serializeToMap(configurationSerializable), JsonConfigurationSerialization.MAP_TYPE.getType());
    }

    @Override
    public ConfigurationSerializable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return JsonConfigurationSerialization.deserializeFromMap(jsonDeserializationContext.deserialize(jsonElement, JsonConfigurationSerialization.MAP_TYPE.getType()));
    }
}
