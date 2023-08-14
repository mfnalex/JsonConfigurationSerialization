# JSON Serializer/Deserializer for Spigot's ConfigurationSerializable
<!--- Buttons start -->
<p align="center">
  <a href="https://hub.jeff-media.com/javadocs/com/jeff-media/json-configuration-serialization/1.1.2">
    <img src="https://static.jeff-media.com/img/button_javadocs.png?3" alt="Javadocs">
  </a>
  <a href="https://discord.jeff-media.com/">
    <img src="https://static.jeff-media.com/img/button_discord.png?3" alt="Discord">
  </a>
  <a href="https://paypal.me/mfnalex">
    <img src="https://static.jeff-media.com/img/button_donate.png?3" alt="Donate">
  </a>
</p>
<!--- Buttons end -->

Simple library to serialize and deserialize Spigot's ConfigurationSerializable objects to and from JSON. Compatible with all Spigot versions.

# Usage
You can either use the static utility methods to serialize to/from json Strings:

```java
// Serializing
String serialized = JsonConfigurationSerialization.serialize(someItemStack);

// Deserializing
ItemStack deserialized = (ItemStack) JsonConfigurationSerialization.deserialize(serialized);
```

Or you can use the ConfigurationSerializableTypeHierarchyAdapter directly with Gson:
```java
Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ConfigurationSerializable.class, JsonConfigurationSerialization.TYPE_HIERARCHY_ADAPTER).create();
String serialized = gson.toJson(someItemStack);
ItemStack deserialized = gson.fromJson(serialized, ItemStack.class);
```

# Maven dependency
The dependency is available on Maven Central:
```xml
<dependency>
  <groupId>com.jeff-media</groupId>
  <artifactId>json-configuration-serialization</artifactId>
  <version>1.1.2</version>
</dependency>
```

[Must be shaded into your plugin jar.](https://blog.jeff-media.com/common-maven-questions/)

### Note for ancient Spigot versions
If your version of Spigot does not provide Gson on its classpath, you need to shade a compatible version of it yourself (e.g. com.google.code.gson:gson:2.10.1)