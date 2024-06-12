package gg.auroramc.collections.placeholder;

import gg.auroramc.aurora.api.placeholder.PlaceholderHandler;
import gg.auroramc.collections.AuroraCollections;
import org.bukkit.entity.Player;

import java.util.List;

public class CollectionsPlaceholderHandler implements PlaceholderHandler {
    private final AuroraCollections plugin;

    public CollectionsPlaceholderHandler(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "collections";
    }

    @Override
    public String onPlaceholderRequest(Player player, String[] args) {
        return "";
    }

    @Override
    public List<String> getPatterns() {
        return List.of();
    }
}
