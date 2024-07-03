package gg.auroramc.collections.hooks.customfishing;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.hooks.Hook;
import gg.auroramc.collections.hooks.customfishing.listener.CustomFishingListener;
import org.bukkit.Bukkit;

public class CustomFishingHook implements Hook {
    @Override
    public void hook(AuroraCollections plugin) {
        Bukkit.getPluginManager().registerEvents(new CustomFishingListener(plugin), plugin);
        AuroraCollections.logger().info("Hooked into CustomFishing for fishing collection with namespace 'customfishing'");
    }
}
