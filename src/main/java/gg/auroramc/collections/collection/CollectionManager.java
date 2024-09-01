package gg.auroramc.collections.collection;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.events.user.AuroraUserLoadedEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.message.Text;
import gg.auroramc.aurora.api.reward.*;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.event.CollectionLevelUpEvent;
import gg.auroramc.collections.hooks.HookManager;
import gg.auroramc.collections.hooks.worldguard.WorldGuardHook;
import gg.auroramc.collections.listener.*;
import gg.auroramc.collections.reward.corrector.CommandCorrector;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CollectionManager implements Listener {
    private final AuroraCollections plugin;
    private final Map<String, Map<String, Collection>> categories = Maps.newConcurrentMap();
    @Getter
    private final RewardFactory rewardFactory = new RewardFactory();
    @Getter
    private final RewardAutoCorrector rewardAutoCorrector = new RewardAutoCorrector();
    private final Map<String, Category> categoryMap = Maps.newConcurrentMap();

    public CollectionManager(AuroraCollections plugin) {
        this.plugin = plugin;

        rewardFactory.registerRewardType(NamespacedId.fromDefault("command"), CommandReward.class);
        rewardFactory.registerRewardType(NamespacedId.fromDefault("money"), MoneyReward.class);
        rewardFactory.registerRewardType(NamespacedId.fromDefault("item"), ItemReward.class);

        rewardAutoCorrector.registerCorrector(NamespacedId.fromDefault("command"), new CommandCorrector(plugin));

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityKillListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new FishingListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new HarvestingListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerKillListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new ShearListener(plugin), plugin);
    }

    public java.util.Collection<Category> getCategories() {
        return categoryMap.values();
    }

    public List<Collection> getAllCollections() {
        return categories.values().stream().flatMap(map -> map.values().stream()).toList();
    }

    public Category getCategory(String category) {
        return categoryMap.get(category);
    }

    public java.util.Collection<Collection> getCollectionsForCategory(String category) {
        return categories.get(category).values();
    }

    public int getMaxCategoryLevel(String category) {
        if (!categories.containsKey(category)) return 0;
        return categories.get(category).values().stream().mapToInt(Collection::getMaxLevel).sum();
    }

    public int getCategoryLevel(String category, Player player) {
        if (!categories.containsKey(category)) return 0;
        return categories.get(category).values().stream().mapToInt(c -> c.getPlayerLevel(player)).sum();
    }

    public double getCategoryCompletionPercent(String category, Player player) {
        return getCategoryLevel(category, player) / Math.max(getMaxCategoryLevel(category), 1D);
    }

    public List<Collection> getCollectionsByCategory(String category) {
        return List.copyOf(categories.get(category).values());
    }

    public Collection getCollection(String category, String name) {
        var collectionMap = categories.get(category);
        if (collectionMap != null) {
            return collectionMap.get(name);
        }
        return null;
    }

    public boolean hasCategory(String category) {
        return categories.containsKey(category);
    }

    public void progressCollections(Player player, TypeId type, int amount, String... triggers) {
        if (!player.hasPermission("aurora.collections.use")) return;
        if (plugin.getConfigManager().getConfig().getPreventCreativeMode() && player.getGameMode() == GameMode.CREATIVE)
            return;
        if (!AuroraAPI.getUserManager().getUser(player).isLoaded()) return;

        if (HookManager.isEnabled(WorldGuardHook.class)) {
            if (HookManager.getHook(WorldGuardHook.class).isBlocked(player, player.getLocation())) return;
        }

        CompletableFuture.runAsync(() -> {
            var toUpdate = new HashSet<String>();

            for (var category : categories.values()) {
                for (var collection : category.values()) {
                    if (Arrays.stream(triggers).anyMatch(trigger -> collection.getConfig().getParsedTriggers().contains(trigger))) {
                        collection.progress(player, type, amount);
                        toUpdate.add(collection.getCategory() + "_" + collection.getId());
                        toUpdate.add("cc_" + collection.getCategory());
                    }
                }
            }

            if (!toUpdate.isEmpty()) {
                var user = AuroraAPI.getUserManager().getUser(player);
                if (!user.isLoaded()) return;
                AuroraAPI.getLeaderboards().updateUser(user, toUpdate.toArray(new String[0]));
            }
        });
    }

    public void reloadCollections() {
        categoryMap.clear();
        categories.clear();
        var config = plugin.getConfigManager().getCollections();
        for (var category : config.entrySet()) {
            var categoryMap = Maps.<String, Collection>newLinkedHashMap();
            for (var collection : category.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
                categoryMap.put(collection.getKey(), new Collection(plugin, collection.getValue(), category.getKey(), collection.getKey()));
            }
            categories.put(category.getKey(), categoryMap);
        }

        for (var entry : plugin.getConfigManager().getCategoriesConfig().getCategories().entrySet()) {
            categoryMap.put(entry.getKey(), new Category(entry.getKey(), rewardFactory, entry.getValue()));
            if (!categories.containsKey(entry.getKey())) {
                categories.put(entry.getKey(), Maps.newConcurrentMap());
            }
        }
    }

    @EventHandler
    public void onUserLoaded(AuroraUserLoadedEvent e) {
        rewardAutoCorrector.correctRewards(e.getUser().getPlayer());
    }

    @EventHandler
    public void onCollectionLevelUp(CollectionLevelUpEvent e) {
        var categoryId = e.getCollection().getCategory();
        var category = categoryMap.get(categoryId);

        if (!category.isLevelingEnabled()) return;

        var player = e.getPlayer();

        int level = getCategoryLevel(categoryId, player);
        var rewards = category.getRewards(level - 1, level, getMaxCategoryLevel(categoryId));

        if (rewards.isEmpty()) return;

        double highestPercent = 0;

        var currentPercent = getCategoryCompletionPercent(categoryId, player) * 100;
        for (var r : category.getRewards()) {
            if (r.percentage() > highestPercent && currentPercent >= r.percentage()) {
                highestPercent = r.percentage();
            }
        }

        List<Placeholder<?>> placeholders = List.of(
                Placeholder.of("{player}", player.getName()),
                Placeholder.of("{category_name}", categoryMap.get(categoryId).getConfig().getName()),
                Placeholder.of("{category_id}", categoryId),
                Placeholder.of("{percent}", AuroraAPI.formatNumber(highestPercent))
        );

        var lvlUpMsg = plugin.getConfigManager().getConfig().getCategoryLevelUpMessage();

        if (lvlUpMsg.getEnabled()) {
            var text = Component.text();
            var messageLines = lvlUpMsg.getMessage();
            var mainConfig = plugin.getConfigManager().getConfig();

            for (var line : messageLines) {
                if (line.equals("component:rewards")) {

                    if (!rewards.isEmpty()) {
                        text.append(Text.component(e.getPlayer(), mainConfig.getDisplayComponents().get("rewards").getTitle(), placeholders));
                    }
                    for (var reward : rewards) {
                        text.append(Component.newline());
                        var display = mainConfig.getDisplayComponents().get("rewards").getLine().replace("{reward}", reward.getDisplay(player, placeholders));
                        text.append(Text.component(player, display, placeholders));
                    }
                } else {
                    text.append(Text.component(player, line, placeholders));
                }

                if (!line.equals(messageLines.getLast())) text.append(Component.newline());
            }

            if (lvlUpMsg.getOpenMenuWhenClicked()) {
                text.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/" + mainConfig.getCommandAliases().getCollections().get(0) + " " +
                                mainConfig.getCommandAliases().getProgression().get(0) + " " +
                                category.getId()));
            }

            Chat.sendMessage(player, text.build());
        }

        RewardExecutor.execute(rewards, player, level, placeholders);
    }
}
