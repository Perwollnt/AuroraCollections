package gg.auroramc.collections.config;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.collections.AuroraCollections;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter
public class Config extends AuroraConfig {
    private Boolean debug = false;
    private String language = "en";
    private CommandAliasConfig commandAliases;
    private Map<String, LevelMatcherConfig> globalLevelMatchers;
    private LevelUpSound levelUpSound;
    private LevelUpMessage levelUpMessage;
    private Map<String, DisplayComponent> displayComponents;
    private Boolean preventCreativeMode = false;

    public Config(AuroraCollections plugin) {
        super(getFile(plugin));
    }

    @Getter
    public static final class CommandAliasConfig {
        private List<String> collections = List.of("collections");
    }

    @Getter
    public static final class LevelMatcherConfig {
        private Integer interval;
        private Integer priority;
        private ConfigurationSection rewards;
    }

    @Getter
    public static final class DisplayComponent {
        private String title;
        private String line;
    }

    @Getter
    public static final class LevelUpMessage {
        private Boolean enabled;
        private List<String> message;
    }

    @Getter
    public static final class LevelUpSound {
        private Boolean enabled;
        private String sound;
        private Float volume;
        private Float pitch;
    }

    public static File getFile(AuroraCollections plugin) {
        return new File(plugin.getDataFolder(), "config.yml");
    }

    public static void saveDefault(AuroraCollections plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("config.yml", false);
        }
    }
}
