package gg.auroramc.collections.menu;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.aurora.api.menu.ItemBuilder;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.AuroraCollections;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

        var menu = new AuroraMenu(player, config.getTitle(), config.getRows() * 9, false, menuId);

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

            List<Placeholder<?>> placeholders = new ArrayList<>();

            var boardName = "cc_" + category;
            var lb = AuroraAPI.getUser(player.getUniqueId()).getLeaderboardEntries().get(boardName);
            var lbm = AuroraAPI.getLeaderboards();

            if (lb != null && lb.getPosition() != 0) {
                placeholders.add(Placeholder.of("{lb_position}", AuroraAPI.formatNumber(lb.getPosition())));
                placeholders.add(Placeholder.of("{lb_position_percent}", AuroraAPI.formatNumber(
                        Math.min(((double) lb.getPosition() / Math.max(1, AuroraAPI.getLeaderboards().getBoardSize(boardName))) * 100, 100)
                )));
                placeholders.add(Placeholder.of("{lb_size}",
                        AuroraAPI.formatNumber(
                                Math.max(Math.max(lb.getPosition(), Bukkit.getOnlinePlayers().size()), AuroraAPI.getLeaderboards().getBoardSize(boardName)))));
            } else {
                placeholders.add(Placeholder.of("{lb_position}", lbm.getEmptyPlaceholder()));
                placeholders.add(Placeholder.of("{lb_position_percent}", lbm.getEmptyPlaceholder()));
                placeholders.add(Placeholder.of("{lb_size}",
                        AuroraAPI.formatNumber(Math.max(Bukkit.getOnlinePlayers().size(), AuroraAPI.getLeaderboards().getBoardSize(boardName)))));
            }

            var totalCollected = plugin.getCollectionManager().getCollectionsByCategory(category).stream()
                    .mapToLong(collection -> collection.getCount(player)).sum();

            placeholders.add(Placeholder.of("{total_formatted}", AuroraAPI.formatNumber(totalCollected)));
            placeholders.add(Placeholder.of("{total}", totalCollected));
            placeholders.add(Placeholder.of("{total_short}", AuroraAPI.formatNumberShort(totalCollected)));

            menu.addItem(ItemBuilder.of(item.getValue())
                            .placeholder(Placeholder.of("{name}", categoryName))
                            .placeholder(Placeholder.of("{progress_percent}", currentPercentage))
                            .placeholder(placeholders)
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
