package gg.auroramc.collections.menu;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.aurora.api.menu.ItemBuilder;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.AuroraCollections;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CategoryMenu {
    @Getter
    private final static NamespacedId menuId = NamespacedId.fromDefault("collections_category_menu");

    private final Player player;
    private final AuroraCollections plugin;

    public CategoryMenu(Player player, AuroraCollections plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public void open() {
        createMenu().open();
    }

    private AuroraMenu createMenu() {
        var config = plugin.getConfigManager().getCategoriesMenuConfig();

        var menu = new AuroraMenu(player, config.getTitle(), 54, false, menuId);

        if (config.getFiller().getEnabled()) {
            menu.addFiller(ItemBuilder.of(config.getFiller().getItem()).toItemStack(player));
        } else {
            menu.addFiller(ItemBuilder.filler(Material.AIR));
        }

        for (var customItem : config.getCustomItems().values()) {
            menu.addItem(ItemBuilder.of(customItem).build(player));
        }

        for (var item : config.getItems().entrySet()) {
            var category = item.getKey();
            var categoryName = plugin.getConfigManager().getCategoriesConfig().getCategories().get(category).getName();
            var currentPercentage = AuroraAPI.formatNumber(plugin.getCollectionManager().getCategoryCompletionPercent(category, player) * 100);

            menu.addItem(ItemBuilder.of(item.getValue())
                            .placeholder(Placeholder.of("{name}", categoryName))
                            .placeholder(Placeholder.of("{progress_percent}", currentPercentage))
                            .build(player),
                    (e) -> {
                        if (e.isRightClick() && plugin.getCollectionManager().getCategory(category).isLevelingEnabled()) {
                            new CategoryRewardsMenu(player, plugin, category).open();
                        } else {
                            new CollectionsMenu(player, plugin, category).open();
                        }
                    });
        }

        return menu;
    }
}
