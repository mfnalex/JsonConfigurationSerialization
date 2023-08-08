# JSON Serializer/Deserializer for Spigot's ConfigurationSerializable
<!--- Buttons start -->
<p align="center">
  <a href="https://repo.jeff-media.com/javadoc/public/com/jeff-media/JsonConfigurationSerialization/1.0-SNAPSHOT">
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

### Repository
```xml
<repository>
    <id>jeff-media-public</id>
    <name>JEFF Media GbR Repository</name>
    <url>https://repo.jeff-media.com/public</url>
</repository>
```

### Dependency
```xml
<dependency>
  <groupId>com.jeff-media</groupId>
  <artifactId>JsonConfigurationSerialization</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

[Must be shaded into your plugin jar.](https://blog.jeff-media.com/common-maven-questions/)

# Note for ancient Spigot versions
If your version of Spigot does not provide Gson on its classpath, you need to shade a compatible version of it yourself (e.g. com.google.code.gson:gson:2.10.1)