package gg.auroramc.collections.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.message.Chat;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.menu.CategoryMenu;
import gg.auroramc.collections.menu.CollectionsMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("%collectionsAlias")
public class CollectionsCommand extends BaseCommand {
    private final AuroraCollections plugin;

    public CollectionsCommand(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Opens the collections menu")
    @CommandPermission("aurora.collections.use")
    public void onMenu(Player player) {
        if (!AuroraAPI.getUser(player.getUniqueId()).isLoaded()) {
            Chat.sendMessage(player, plugin.getConfigManager().getMessageConfig().getDataNotLoadedYetSelf());
            return;
        }
        new CategoryMenu(player, plugin).open();
    }

    @Subcommand("reload")
    @Description("Reloads the plugin configs and applies reward auto correctors to players")
    @CommandPermission("aurora.collections.admin.reload")
    public void onReload(CommandSender sender) {
        plugin.reload();
        Chat.sendMessage(sender, plugin.getConfigManager().getMessageConfig().getReloaded());
    }

    @Subcommand("open")
    @Description("Opens the collections menu for another player in a specific category")
    @CommandCompletion("@players @categories true|false")
    @CommandPermission("aurora.collections.admin.open")
    public void onOpenMenu(CommandSender sender, @Flags("other") Player target, @Default("none") String category, @Default("false") Boolean silent) {
        if(category.equals("none")) {
            new CategoryMenu(target, plugin).open();
        } else {
            new CollectionsMenu(target, plugin, category).open();
        }

        if(!silent) {
            Chat.sendMessage(sender, plugin.getConfigManager().getMessageConfig().getMenuOpened(), Placeholder.of("{player}", target.getName()));
        }
    }
}
