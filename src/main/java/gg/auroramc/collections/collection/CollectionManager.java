package gg.auroramc.collections.collection;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.dependency.Dep;
import gg.auroramc.aurora.api.dependency.DependencyManager;
import gg.auroramc.aurora.api.events.user.AuroraUserLoadedEvent;
import gg.auroramc.aurora.api.reward.CommandReward;
import gg.auroramc.aurora.api.reward.MoneyReward;
import gg.auroramc.aurora.api.reward.RewardAutoCorrector;
import gg.auroramc.aurora.api.reward.RewardFactory;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.hooks.HookManager;
import gg.auroramc.collections.hooks.worldguard.WorldGuardHook;
import gg.auroramc.collections.listener.*;
import gg.auroramc.collections.reward.corrector.CommandCorrector;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
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

    public CollectionManager(AuroraCollections plugin) {
        this.plugin = plugin;

        rewardFactory.registerRewardType(NamespacedId.fromDefault("command"), CommandReward.class);
        rewardFactory.registerRewardType(NamespacedId.fromDefault("money"), MoneyReward.class);

        rewardAutoCorrector.registerCorrector(NamespacedId.fromDefault("command"), new CommandCorrector(plugin));

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

    public void progressCollections(Player player, TypeId type, int amount, Trigger... triggers) {
        if (plugin.getConfigManager().getConfig().getPreventCreativeMode() && player.getGameMode() == GameMode.CREATIVE)
            return;
        if (!AuroraAPI.getUserManager().getUser(player).isLoaded()) return;

        if (HookManager.isEnabled(WorldGuardHook.class)) {
            if (HookManager.getHook(WorldGuardHook.class).isBlocked(player, player.getLocation())) return;
        }

        CompletableFuture.runAsync(() -> {
            for (var category : categories.values()) {
                for (var collection : category.values()) {
                    if (Arrays.stream(triggers).anyMatch(trigger -> collection.getConfig().getParsedTriggers().contains(trigger))) {
                        collection.progress(player, type, amount);
                    }
                }
            }
        });
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
        rewardAutoCorrector.correctRewards(e.getUser().getPlayer());
    }
}
