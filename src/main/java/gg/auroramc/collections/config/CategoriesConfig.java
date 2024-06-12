package gg.auroramc.collections.config;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.collections.AuroraCollections;
import lombok.Getter;

import java.io.File;
import java.util.Map;

@Getter
public class CategoriesConfig extends AuroraConfig {
    private Map<String, String> categories;

    public CategoriesConfig(AuroraCollections plugin) {
        super(getFile(plugin));
    }

    public static File getFile(AuroraCollections plugin) {
        return new File(plugin.getDataFolder(), "categories.yml");
    }

    public static void saveDefault(AuroraCollections plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("categories.yml", false);
        }
    }
}
