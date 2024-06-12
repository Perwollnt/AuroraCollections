package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.collection.TypeId;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import net.momirealms.customfishing.api.common.Pair;
import net.momirealms.customfishing.api.event.FishingResultEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CustomFishingListener implements Listener {
    private final AuroraCollections plugin;

    public CustomFishingListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(FishingResultEvent e) {
        if (e.getLoot() == null || e.getLoot().getID() == null) return;
        if (e.getResult() != FishingResultEvent.Result.SUCCESS) return;

        var itemId = new TypeId("customfishing", e.getLoot().getID());
        var quantity = e.getAmount();
        var player = e.getPlayer();
        var isVanillaLoot = itemId.equals("vanilla");

        if (isVanillaLoot) {
            var manager = CustomFishingPlugin.get().getFishingManager();
            var managerClass = manager.getClass();
            try {
                var field = managerClass.getDeclaredField("vanillaLootMap");
                field.setAccessible(true);
                var map = (Map<UUID, Pair<ItemStack, Integer>>) field.get(manager);
                var loot = map.get(player.getUniqueId());
                itemId = TypeId.from(loot.left().getType());
            } catch (Exception error) {
                AuroraCollections.logger().warning("Failed to get vanilla loot map from CustomFishing, error: " + error.getMessage());
            }
        }

        TypeId finalItemId = itemId;

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();
            manager.progressCollections(player, Trigger.FISH, finalItemId, quantity);
        });
    }
}
