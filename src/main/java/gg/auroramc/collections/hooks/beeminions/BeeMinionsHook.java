package gg.auroramc.collections.hooks.beeminions;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.me.leo_s.beeminions.api.events.RemoveItemStorageMinion;

public class BeeMinionsHook implements Hook, Listener {
    private AuroraCollections plugin;

    @Override
    public void hook(AuroraCollections plugin) {
        this.plugin = plugin;
        AuroraCollections.logger().info("Hooked into BeeMinions with trigger: minion_loot");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemTakeOut(RemoveItemStorageMinion event) {
        if (event.getItemAffected() == null) return;

        var player = Bukkit.getPlayer(event.getOwner());
        if (player == null) return;

        var amount = event.getItemAffected().getAmount();

        ItemStack itemStack = event.getItemAffected();
        plugin.getCollectionManager().progressCollections(player, itemStack, amount, Trigger.MINION_LOOT);
    }
}
