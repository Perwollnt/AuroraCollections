package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.collection.TypeId;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

public class MythicMobsListener implements Listener {
    private final AuroraCollections plugin;

    public MythicMobsListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        if (!(e.getKiller() instanceof Player player)) return;

        var mobName = e.getMob().getType().getInternalName();
        var drops = e.getDrops();

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();
            var itemManager = MythicBukkit.inst().getItemManager();

            manager.progressCollections(player, Trigger.ENTITY_KILL, new TypeId("mythicmobs", mobName), 1);

            for (var drop : drops) {
                if(itemManager.isMythicItem(drop)) {
                    var mythicType = itemManager.getMythicTypeFromItem(drop);
                    if(mythicType != null) {
                        manager.progressCollections(player, Trigger.ENTITY_LOOT, new TypeId("mythicmobs", mythicType), drop.getAmount());
                    }
                } else {
                    manager.progressCollections(player, Trigger.ENTITY_LOOT, TypeId.from(drop.getType()), drop.getAmount());
                }
            }
        });
    }
}
