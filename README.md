# AuroraCollections

Highly customizable and feature-rich Hypixel like collection leveling plugin for Paper servers.
Let your players level up collections by collecting multiple items from the same type and give them rewards like money, 
items, permissions, or even custom rewards.

## Features:
- Template based leveling system, so you don't need to configure all day long
- Multiple reward types and integrations
- Customizable GUI menus
- Automatic reward correction if you change your configs later on
- support math formulas in rewards
- built in money/command/permission/AuraSkills-stat reward types
- Mythic(Mobs) custom mechanics and conditions so your mobs can give progress to your collections. You can even check if a player has a certain collection level or not with MythicCrucible.
- PlaceholderAPI support
- Multiple economy support


## Developer API

### Maven

```xml
<repository>
    <id>auroramc</id>
    <url>https://repo.auroramc.gg/repository/maven-public/</url>
</repository>
```

```xml
<dependency>
    <groupId>gg.auroramc</groupId>
    <artifactId>AuroraCollections</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```
### Gradle

**Groovy DSL:**
```gradle
repositories {
    maven {
        url "https://repo.auroramc.gg/repository/maven-public/"
    }
}

dependencies {
    compileOnly 'gg.auroramc:AuroraCollections:1.0.0-SNAPSHOT'
}
```

**Kotlin DSL:**
```Gradle Kotlin DSL
repositories { 
    maven("https://repo.auroramc.gg/repository/maven-public/")
}

dependencies { 
    compileOnly("gg.auroramc:AuroraCollections:1.0.0-SNAPSHOT")
}
```