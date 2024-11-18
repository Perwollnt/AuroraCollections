package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;


public class FishingListener implements Listener {
    private final AuroraCollections plugin;

    public FishingListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent e) {
        if (e.getCaught() == null) return;
        if (!(e.getCaught() instanceof Item item)) return;

        var manager = plugin.getCollectionManager();

        manager.progressCollections(e.getPlayer(), item.getItemStack(), Trigger.FISH);
    }
}
