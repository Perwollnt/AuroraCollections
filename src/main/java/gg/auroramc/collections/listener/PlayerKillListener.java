package gg.auroramc.collections.listener;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.collection.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerKillListener implements Listener {
    private final AuroraCollections plugin;

    public PlayerKillListener(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        var killer = e.getEntity().getKiller();
        if (killer == null) return;
        if (e.getEntity().equals(e.getEntity().getKiller())) return;

        CompletableFuture.runAsync(() -> {
            var manager = plugin.getCollectionManager();
            manager.progressCollections(killer, Trigger.PLAYER_KILL, null, 1);
        });
    }
}

