package gg.auroramc.collections.hooks.mythic.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import gg.auroramc.collections.collection.TypeId;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


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

        var manager = plugin.getCollectionManager();
        var itemManager = MythicBukkit.inst().getItemManager();

        manager.progressCollections(player, new TypeId("mythicmobs", mobName), 1, Trigger.ENTITY_KILL);

        for (var drop : drops) {
            if (itemManager.isMythicItem(drop)) {
                var mythicType = itemManager.getMythicTypeFromItem(drop);
                if (mythicType != null) {
                    manager.progressCollections(player, new TypeId("mythicmobs", mythicType), drop.getAmount(), Trigger.ENTITY_LOOT);
                }
            } else {
                manager.progressCollections(player, TypeId.from(drop.getType()), drop.getAmount(), Trigger.ENTITY_LOOT);
            }
        }
    }
}
