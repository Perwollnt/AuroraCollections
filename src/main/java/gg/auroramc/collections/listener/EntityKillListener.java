package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.collection.TypeId;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.concurrent.CompletableFuture;


public class EntityKillListener implements Listener {
    private final AuroraCollections plugin;

    public EntityKillListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        var killer = e.getEntity().getKiller();
        if (killer == null) return;
        if (e.getEntity() instanceof Player) return;
        var drops = e.getDrops();

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();
            manager.progressCollections(killer, Trigger.ENTITY_KILL, TypeId.from(e.getEntity().getType()), 1);

            for (var drop : drops) {
                manager.progressCollections(killer, Trigger.ENTITY_LOOT, TypeId.from(drop.getType()), drop.getAmount());
            }
        });
    }
}
