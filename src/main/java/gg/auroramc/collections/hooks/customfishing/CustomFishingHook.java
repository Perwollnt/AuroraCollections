package gg.auroramc.collections.hooks.customfishing;

import gg.auroramc.aurora.api.dependency.Dep;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.config.CollectionConfig;
import gg.auroramc.collections.hooks.Hook;
import gg.auroramc.collections.hooks.customfishing.listener.CustomFishingListener;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.loot.LootType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CustomFishingHook implements Hook {

    @Override
    public void hook(AuroraCollections plugin) {
        Bukkit.getPluginManager().registerEvents(new CustomFishingListener(plugin), plugin);
        plugin.getItemManager().registerResolver(Dep.CUSTOMFISHING, new CustomFishingItemResolver());
        if (!plugin.getConfigManager().getMetaConfig().isCustomFishingCollectionsSaved()) {
            generateDefaultCollections(plugin);
            plugin.getConfigManager().getMetaConfig().setCustomFishingCollectionsSaved(true);
            plugin.getConfigManager().getMetaConfig().saveChanges();
            plugin.getCollectionManager().reloadCollections();
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

            var itemStack = CustomFishingPlugin.get().getItemManager().build(null, "item", loot.getID(), new HashMap<String, String>());

            yaml.set("menu-item.material", itemStack.getType().toString());
            if (itemStack.getItemMeta().hasCustomModelData()) {
                yaml.set("menu-item.custom-model-data", itemStack.getItemMeta().getCustomModelData());
            }
            if (!itemStack.getItemMeta().getItemFlags().isEmpty()) {
                yaml.set("menu-item.flags", itemStack.getItemMeta().getItemFlags().stream().map(Enum::name).toList());
            }
            if (itemStack.getType() == Material.PLAYER_HEAD && itemStack.getItemMeta() instanceof SkullMeta meta) {
                if (meta.getOwnerProfile() != null) {
                    if (meta.getOwnerProfile().getTextures().getSkin() != null) {
                        yaml.set("menu-item.skull.url", meta.getOwnerProfile().getTextures().getSkin().toString());
                    }
                }
            }


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
