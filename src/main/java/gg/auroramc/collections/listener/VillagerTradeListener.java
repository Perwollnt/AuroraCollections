package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.CollectionManager;
import gg.auroramc.collections.collection.Trigger;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VillagerTradeListener implements Listener {
    private final AuroraCollections plugin;

    public VillagerTradeListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTrade(PlayerPurchaseEvent event) {

        Player player = event.getPlayer();

        CollectionManager manager = plugin.getCollectionManager();

        manager.progressCollections(player, event.getTrade().getResult(), Trigger.VILLAGER_TRADE);
    }
}
