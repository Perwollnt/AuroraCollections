package gg.auroramc.collections.config.menu;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.premade.ItemConfig;
import gg.auroramc.collections.AuroraCollections;
import lombok.Getter;

import java.io.File;
import java.util.Map;

@Getter
public class CategoriesMenuConfig extends AuroraConfig {
    private String title;
    private FillerItem filler;
    private Map<String, ItemConfig> items;
    private Map<String, ItemConfig> customItems;
    private Integer rows = 6;

    @Getter
    public static final class FillerItem {
        private Boolean enabled;
        private ItemConfig item;
    }

    public CategoriesMenuConfig(AuroraCollections plugin) {
        super(getFile(plugin));
    }

    public static File getFile(AuroraCollections plugin) {
        return new File(plugin.getDataFolder() + "/menus", "categories.yml");
    }

    public static void saveDefault(AuroraCollections plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("menus/categories.yml", false);
        }
    }
}
