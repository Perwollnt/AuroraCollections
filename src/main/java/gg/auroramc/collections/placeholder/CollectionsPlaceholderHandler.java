package gg.auroramc.collections.placeholder;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.placeholder.PlaceholderHandler;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.data.CollectionData;
import gg.auroramc.collections.util.RomanNumber;
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
        if (args.length < 3) return null;
        var manager = plugin.getCollectionManager();

        var category = args[0].replace(":", "_");
        var id = args[1].replace(":", "_");
        var type = args[2];
        var isRaw = args.length > 3 && args[3].equals("raw");
        var romanNumeral = plugin.getConfigManager().getCollectionMenuConfig().getForceRomanNumerals();

        switch (type) {
            case "name" -> {
                var collection = manager.getCollection(category, id);
                if (collection == null) return null;
                return collection.getConfig().getName();
            }
            case "level" -> {
                var collection = manager.getCollection(category, id);
                if (collection == null) return null;
                var level = collection.getPlayerLevel(player);
                return isRaw ? String.valueOf(level) : romanNumeral ? RomanNumber.toRoman((long) level) : AuroraAPI.formatNumber(level);
            }
            case "count" -> {
                var collection = manager.getCollection(category, id);
                if (collection == null) return null;
                var count = AuroraAPI.getUserManager().getUser(player).getData(CollectionData.class).getCollectionCount(category, id);
                return isRaw ? String.valueOf(count) : AuroraAPI.formatNumber(count);
            }
            case "next_count" -> {
                var collection = manager.getCollection(category, id);
                if (collection == null) return null;
                var nextCount = collection.getRequiredAmount(collection.getPlayerLevel(player) + 1);
                return isRaw ? String.valueOf(nextCount) : AuroraAPI.formatNumber(nextCount);
            }
        }

        return null;
    }

    @Override
    public List<String> getPatterns() {
        var manager = plugin.getCollectionManager();

        return manager.getAllCollections().stream().map(c -> List.of(
                        c.getCategory().replace("_", ":") + "_" + c.getId().replace("_", ":") + "_name",
                        c.getCategory().replace("_", ":") + "_" + c.getId().replace("_", ":") + "_level",
                        c.getCategory().replace("_", ":") + "_" + c.getId().replace("_", ":") + "_level_raw",
                        c.getCategory().replace("_", ":") + "_" + c.getId().replace("_", ":") + "_count",
                        c.getCategory().replace("_", ":") + "_" + c.getId().replace("_", ":") + "_count_raw",
                        c.getCategory().replace("_", ":") + "_" + c.getId().replace("_", ":") + "_next_count",
                        c.getCategory().replace("_", ":") + "_" + c.getId().replace("_", ":") + "_next_count_raw"))
                .flatMap(List::stream).toList();
    }
}
