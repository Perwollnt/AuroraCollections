package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.collection.TypeId;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.concurrent.CompletableFuture;

public class ShearListener implements Listener {
    private final AuroraCollections plugin;

    public ShearListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShear(PlayerShearBlockEvent event) {
        var player = event.getPlayer();
        var drops = event.getDrops();

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();
            for (var drop : drops) {
                manager.progressCollections(player, Trigger.BLOCK_SHEAR_LOOT, TypeId.from(drop.getType()), drop.getAmount());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        var player = event.getPlayer();
        var drops = event.getDrops();

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();
            for (var drop : drops) {
                manager.progressCollections(player, Trigger.SHEAR_LOOT, TypeId.from(drop.getType()), drop.getAmount());
            }
        });
    }
}
