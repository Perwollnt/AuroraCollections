package gg.auroramc.collections.api.item;

import gg.auroramc.aurora.api.dependency.Dep;
import gg.auroramc.collections.collection.TypeId;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemManager {
    private final Map<String, ItemResolver> resolvers = new LinkedHashMap<>();

    public void registerResolver(String plugin, ItemResolver resolver) {
        resolvers.put(plugin, resolver);
    }

    public void registerResolver(Dep plugin, ItemResolver resolver) {
        resolvers.put(plugin.getId(), resolver);
    }

    public ItemResolver getResolver(String plugin) {
        return resolvers.get(plugin);
    }

    public void unregisterResolver(String plugin) {
        resolvers.remove(plugin);
    }

    public TypeId resolveId(ItemStack item) {
        for (ItemResolver resolver : resolvers.values()) {
            if (resolver.matches(item)) {
                return resolver.resolveId(item);
            }
        }
        return TypeId.from(item.getType());
    }
}
