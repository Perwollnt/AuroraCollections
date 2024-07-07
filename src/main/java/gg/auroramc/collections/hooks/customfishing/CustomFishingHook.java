package gg.auroramc.collections.hooks.customfishing;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.config.CollectionConfig;
import gg.auroramc.collections.hooks.Hook;
import gg.auroramc.collections.hooks.customfishing.listener.CustomFishingListener;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.loot.LootType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CustomFishingHook implements Hook {

    @Override
    public void hook(AuroraCollections plugin) {
        Bukkit.getPluginManager().registerEvents(new CustomFishingListener(plugin), plugin);
        if (!plugin.getConfigManager().getMetaConfig().isCustomFishingCollectionsSaved()) {
            generateDefaultCollections(plugin);
            plugin.getConfigManager().getMetaConfig().setCustomFishingCollectionsSaved(true);
            plugin.getConfigManager().getMetaConfig().saveChanges();
            AuroraCollections.logger().info("Generated default fishing collections for CustomFishing");
        }
        AuroraCollections.logger().info("Hooked into CustomFishing for fishing collection with namespace 'customfishing'");
    }

    private void generateDefaultCollections(AuroraCollections plugin) {
        for (var loot : CustomFishingPlugin.get().getLootManager().getAllLoots()) {
            if (loot.getType() != LootType.ITEM) continue;
            if (Arrays.stream(loot.getLootGroup()).noneMatch(s -> s.contains("river") || s.contains("ocean"))) continue;

            var item = CustomFishingPlugin.get().getItemManager().getBuildableItem("item", loot.getID());
            if (item == null) continue;

            var file = new File(plugin.getDataFolder(), "collections/fishing/0005_cf_" + loot.getID() + ".yml");
            if (file.exists()) continue;

            var yaml = new YamlConfiguration();
            yaml.set("triggers", List.of("fish"));
            yaml.set("types", List.of("customfishing:" + loot.getID()));
            yaml.set("name", loot.getNick());

            yaml.set("menu-item.material", "customfishing:" + loot.getID());

            yaml.set("requirements", List.of(50, 100, 250, 1000, 2500, 5000, 10000));
            yaml.set("use-global-level-matchers", true);

            try {
                file.createNewFile();
                yaml.save(file);
                var config = new CollectionConfig(file);
                config.load();
                plugin.getConfigManager().getCollections().get("fishing").put("0005_cf_" + loot.getID(), config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
