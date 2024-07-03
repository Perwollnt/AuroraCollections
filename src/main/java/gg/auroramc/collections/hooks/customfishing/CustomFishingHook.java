package gg.auroramc.collections.hooks.customfishing;

import gg.auroramc.aurora.api.dependency.Dep;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.hooks.Hook;
import gg.auroramc.collections.hooks.customfishing.listener.CustomFishingListener;
import org.bukkit.Bukkit;

public class CustomFishingHook implements Hook {
    @Override
    public void hook(AuroraCollections plugin) {
        Bukkit.getPluginManager().registerEvents(new CustomFishingListener(plugin), plugin);
        plugin.getItemManager().registerResolver(Dep.CUSTOMFISHING, new CustomFishingItemResolver());
        AuroraCollections.logger().info("Hooked into CustomFishing for fishing collection with namespace 'customfishing'");
    }
}
