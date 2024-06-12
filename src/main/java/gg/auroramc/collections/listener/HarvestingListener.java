package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.collection.TypeId;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HarvestingListener implements Listener {
    private final AuroraCollections plugin;
    private final List<Material> crops = List.of(Material.WHEAT, Material.POTATOES, Material.CARROTS, Material.BEETROOTS, Material.COCOA, Material.NETHER_WART);

    public HarvestingListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHarvest(PlayerHarvestBlockEvent e) {
        // This event will only be called for right click harvestable crops
        var harvested = e.getItemsHarvested();

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();
            for (var item : harvested) {
                manager.progressCollections(e.getPlayer(), Trigger.HARVEST, TypeId.from(item.getType()), item.getAmount());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBlockBreakHarvest(BlockBreakEvent e) {
        if (!crops.contains(e.getBlock().getType())) return;

        if (e.getBlock().getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() != ageable.getMaximumAge()) return;
            var drops = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand());

            CompletableFuture.runAsync(() -> {
                var manager = plugin.getCollectionManager();
                for (var drop : drops) {
                    manager.progressCollections(e.getPlayer(), Trigger.HARVEST, TypeId.from(drop.getType()), drop.getAmount());
                }
            });
        }
    }
}

