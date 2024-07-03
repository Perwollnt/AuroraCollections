package gg.auroramc.collections.api.item;

import gg.auroramc.collections.collection.TypeId;
import org.bukkit.inventory.ItemStack;

public interface ItemResolver {
    boolean matches(ItemStack item);
    TypeId resolveId(ItemStack item);
}
