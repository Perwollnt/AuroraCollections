package gg.auroramc.collections.collection;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.dependency.Dep;
import gg.auroramc.aurora.api.dependency.DependencyManager;
import gg.auroramc.aurora.api.events.user.AuroraUserLoadedEvent;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.reward.RewardAutoCorrector;
import gg.auroramc.collections.api.reward.RewardFactory;
import gg.auroramc.collections.listener.*;
import gg.auroramc.collections.reward.CommandReward;
import gg.auroramc.collections.reward.MoneyReward;
import gg.auroramc.collections.reward.corrector.CommandCorrector;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

public class CollectionManager implements Listener {
    private final AuroraCollections plugin;
    private final Map<String, Map<String, Collection>> categories = Maps.newConcurrentMap();

    public CollectionManager(AuroraCollections plugin) {
        this.plugin = plugin;

        RewardFactory.registerRewardType(NamespacedId.fromDefault("command"), CommandReward.class);
        RewardFactory.registerRewardType(NamespacedId.fromDefault("money"), MoneyReward.class);

        RewardAutoCorrector.registerCorrector(NamespacedId.fromDefault("command"), new CommandCorrector(plugin));

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityKillListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new FishingListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new HarvestingListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerKillListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new ShearListener(plugin), plugin);

        if (DependencyManager.hasDep(Dep.CUSTOMFISHING)) {
            Bukkit.getPluginManager().registerEvents(new CustomFishingListener(plugin), plugin);
            AuroraCollections.logger().info("Hooked into CustomFishing for fishing collection with namespace 'customfishing'");
        }

        if (DependencyManager.hasDep(Dep.AURASKILLS)) {
            Bukkit.getPluginManager().registerEvents(new AuraSkillsListener(plugin), plugin);
            AuroraCollections.logger().info("Hooked into AuraSkills for handling extra loot drops");
        }

        if (DependencyManager.hasDep(Dep.MYTHICMOBS)) {
            Bukkit.getPluginManager().registerEvents(new MythicMobsListener(plugin), plugin);
            AuroraCollections.logger().info("Hooked into MythicMobs for entity_kill and entity_loot collections with namespace 'mythicmobs'");
        }
    }

    public List<Collection> getAllCollections() {
        return categories.values().stream().flatMap(map -> map.values().stream()).toList();
    }

    public List<Collection> getCollectionsByCategory(String category) {
        return List.copyOf(categories.get(category).values());
    }

    public Collection getCollection(String category, String name) {
        return categories.get(category).get(name);
    }

    public void progressCollections(Player player, Trigger trigger, TypeId type, int amount) {
        if (plugin.getConfigManager().getConfig().getPreventCreativeMode() && player.getGameMode() == GameMode.CREATIVE)
            return;
        if (!AuroraAPI.getUserManager().getUser(player).isLoaded()) return;

        for (var category : categories.values()) {
            for (var collection : category.values()) {
                if (collection.getConfig().getParsedTriggers().contains(trigger)) {
                    collection.progress(player, type, amount);
                }
            }
        }
    }

    public void reloadCollections() {
        categories.clear();
        var config = plugin.getConfigManager().getCollections();
        for (var category : config.entrySet()) {
            var categoryMap = Maps.<String, Collection>newConcurrentMap();
            for (var collection : category.getValue().entrySet()) {
                categoryMap.put(collection.getKey(), new Collection(plugin, collection.getValue(), category.getKey(), collection.getKey()));
            }
            categories.put(category.getKey(), categoryMap);
        }
    }

    @EventHandler
    public void onUserLoaded(AuroraUserLoadedEvent e) {
        RewardAutoCorrector.correctRewards(this, e.getUser().getPlayer());
    }
}
