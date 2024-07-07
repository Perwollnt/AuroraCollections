package gg.auroramc.collections.listener;

import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class BlockBreakListener implements Listener {
    private final AuroraCollections plugin;

    private final Set<Material> crops = Set.of(Material.SUGAR_CANE, Material.CACTUS, Material.BAMBOO);

    private final Set<Material> specialCrops = Set.of(Material.WARPED_FUNGUS, Material.CRIMSON_FUNGUS, Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM, Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.MELON, Material.PUMPKIN);

    public BlockBreakListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(RegionBlockBreakEvent e) {
        if (!e.isNatural()) return;
        var player = e.getPlayerWhoBroke();
        var type = e.getBlock().getType();

        var manager = plugin.getCollectionManager();

        if (crops.contains(type)) {
            manager.progressCollections(player, TypeId.from(type), 1, Trigger.HARVEST);
            return;
        }

        if (specialCrops.contains(type)) {
            for (var drop : e.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                manager.progressCollections(player, TypeId.from(drop.getType()), drop.getAmount(), Trigger.HARVEST);
            }
            return;
        }

        for (var drop : e.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
            manager.progressCollections(player, TypeId.from(drop.getType()), drop.getAmount(), Trigger.BLOCK_LOOT);
        }
    }
}
