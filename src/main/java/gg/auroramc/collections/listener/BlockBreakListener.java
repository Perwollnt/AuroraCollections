package gg.auroramc.collections.listener;

import gg.auroramc.aurora.api.events.region.RegionBlockBreakEvent;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.collection.TypeId;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BlockBreakListener implements Listener {
    private final AuroraCollections plugin;

    private final Set<Material> crops = Set.of(Material.SUGAR_CANE, Material.CACTUS, Material.BAMBOO);
    private final Set<Material> ores = Set.of(Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.COAL_ORE,
            Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.COPPER_ORE,
            Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_COPPER_ORE, Material.AMETHYST_CLUSTER
    );

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

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();

            if (crops.contains(type)) {
                manager.progressCollections(player, Trigger.HARVEST, TypeId.from(type), 1);
                return;
            }

            if (ores.contains(type)) {
                for (var drop : e.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                    manager.progressCollections(player, Trigger.BLOCK_LOOT, TypeId.from(drop.getType()), drop.getAmount());
                }
                return;
            }

            if (specialCrops.contains(type)) {
                for (var drop : e.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                    manager.progressCollections(player, Trigger.HARVEST, TypeId.from(drop.getType()), drop.getAmount());
                }
                return;
            }

            manager.progressCollections(player, Trigger.BLOCK_LOOT, TypeId.from(type), 1);
        });
    }
}
