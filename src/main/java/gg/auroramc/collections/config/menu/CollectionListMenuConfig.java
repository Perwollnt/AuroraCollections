package gg.auroramc.collections.config.menu;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.premade.ItemConfig;
import gg.auroramc.collections.AuroraCollections;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter
public class CollectionListMenuConfig extends AuroraConfig {
    private String title;
    private Map<String, ItemConfig> customItems;
    private List<Integer> displayArea;
    private Items items;

    @Getter
    public static final class Items {
        private FillerItem filler;
        private ItemConfig previousPage;
        private ItemConfig currentPage;
        private ItemConfig nextPage;
        private ItemConfig back;
    }

    @Getter
    public static final class FillerItem {
        private Boolean enabled;
        private ItemConfig item;
    }

    public CollectionListMenuConfig(AuroraCollections plugin) {
        super(getFile(plugin));
    }

    public static File getFile(AuroraCollections plugin) {
        return new File(plugin.getDataFolder() + "/menus", "collection_list.yml");
    }

    public static void saveDefault(AuroraCollections plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("menus/collection_list.yml", false);
        }
    }
}
