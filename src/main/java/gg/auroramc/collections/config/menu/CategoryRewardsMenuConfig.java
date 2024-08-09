package gg.auroramc.collections.config.menu;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.premade.ItemConfig;
import gg.auroramc.collections.AuroraCollections;
import lombok.Getter;

import java.io.File;
import java.util.List;
import java.util.Map;

@Getter
public class CategoryRewardsMenuConfig extends AuroraConfig {
    private String title;
    private ProgressBar progressBar;
    private Map<String, ItemConfig> customItems;
    private Map<String, DisplayComponent> displayComponents;
    private List<Integer> displayArea;
    private Items items;

    public CategoryRewardsMenuConfig(AuroraCollections plugin) {
        super(getFile(plugin));
    }


    @Getter
    public static final class DisplayComponent {
        private String title;
        private String line;
    }

    @Getter
    public static final class Items {
        private FillerItem filler;
        private ItemConfig previousPage;
        private ItemConfig currentPage;
        private ItemConfig nextPage;
        private ItemConfig completedLevel;
        private ItemConfig lockedLevel;
        private ItemConfig back;
    }

    @Getter
    public static final class FillerItem {
        private Boolean enabled;
        private ItemConfig item;
    }

    @Getter
    public static final class ProgressBar {
        private Integer length = 20;
        private String filledCharacter;
        private String unfilledCharacter;
    }

    public static File getFile(AuroraCollections plugin) {
        return new File(plugin.getDataFolder() + "/menus", "category_rewards.yml");
    }

    public static void saveDefault(AuroraCollections plugin) {
        if (!getFile(plugin).exists()) {
            plugin.saveResource("menus/category_rewards.yml", false);
        }
    }

}
