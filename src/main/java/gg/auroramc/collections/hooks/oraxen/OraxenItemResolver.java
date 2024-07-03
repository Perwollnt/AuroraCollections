package gg.auroramc.collections.hooks.oraxen;

import gg.auroramc.collections.api.item.ItemResolver;
import gg.auroramc.collections.collection.TypeId;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenItemResolver implements ItemResolver {
    @Override
    public boolean matches(ItemStack item) {
        return OraxenItems.getIdByItem(item) != null;
    }

    @Override
    public TypeId resolveId(ItemStack item) {
        return new TypeId("oraxen", OraxenItems.getIdByItem(item));
    }
}
