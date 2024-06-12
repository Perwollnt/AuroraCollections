package gg.auroramc.collections;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.AuroraLogger;
import gg.auroramc.aurora.api.menu.AuroraMenu;
import gg.auroramc.collections.api.AuroraCollectionsProvider;
import gg.auroramc.collections.api.data.CollectionData;
import gg.auroramc.collections.api.reward.RewardAutoCorrector;
import gg.auroramc.collections.collection.CollectionManager;
import gg.auroramc.collections.command.CommandManager;
import gg.auroramc.collections.config.ConfigManager;
import gg.auroramc.collections.hooks.HookManager;
import gg.auroramc.collections.menu.CategoryMenu;
import gg.auroramc.collections.menu.CollectionsMenu;
import gg.auroramc.collections.menu.ProgressionMenu;
import gg.auroramc.collections.placeholder.CollectionsPlaceholderHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraCollections extends JavaPlugin {

    @Getter
    private ConfigManager configManager;
    private CommandManager commandManager;
    @Getter
    private CollectionManager collectionManager;

    private static AuroraLogger l;

    public static AuroraLogger logger() {
        return l;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);

        l = AuroraAPI.createLogger("AuroraCollections", () -> configManager.getConfig().getDebug());
        AuroraAPI.getUserManager().registerUserDataHolder(CollectionData.class);
        AuroraAPI.registerPlaceholderHandler(new CollectionsPlaceholderHandler(this));

        commandManager = new CommandManager(this);
        commandManager.reload();

        HookManager.registerHooks(this);

        Bukkit.getGlobalRegionScheduler().run(this, (task) -> {
            collectionManager = new CollectionManager(this);
            collectionManager.reloadCollections();
        });

        try {
            var field = AuroraCollectionsProvider.class.getDeclaredField("plugin");
            field.setAccessible(true);
            field.set(null, this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            l.severe("Failed to initialize api provider!");
            e.printStackTrace();
        }
    }

    public void reload() {
        configManager.reload();
        commandManager.reload();
        collectionManager.reloadCollections();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof AuroraMenu menu) {
                if (menu.getId().equals(CollectionsMenu.getMenuId())) {
                    player.closeInventory();
                } else if (menu.getId().equals(ProgressionMenu.getMenuId())) {
                    player.closeInventory();
                } else if (menu.getId().equals(CategoryMenu.getMenuId())) {
                    new CategoryMenu(player, this).open();
                }
            }
            RewardAutoCorrector.correctRewards(collectionManager, player);
        });
    }

    @Override
    public void onDisable() {
        commandManager.unregisterCommands();
    }
}
