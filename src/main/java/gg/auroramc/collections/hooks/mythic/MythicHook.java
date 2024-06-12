package gg.auroramc.collections.hooks.mythic;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.hooks.Hook;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicHook implements Hook, Listener {
    private MythicRegistrar registrar;

    @Override
    public void hook(AuroraCollections plugin) {
        this.registrar = new MythicRegistrar(plugin);
        AuroraCollections.logger().info("Hooked into MythicMobs for custom mechanics (addToCollection, progressCollection) and conditions (hasCollectionLevel)");
    }

    @EventHandler
    public void onMechanicLoad(MythicMechanicLoadEvent event) {
        registrar.registerApplicableMechanic(event);
    }

    @EventHandler
    public void onConditionLoad(MythicConditionLoadEvent event) {
        registrar.registerApplicableCondition(event);
    }
}
