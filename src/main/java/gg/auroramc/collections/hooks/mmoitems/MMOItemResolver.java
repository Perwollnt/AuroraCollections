package gg.auroramc.collections.hooks.mmoitems;

import gg.auroramc.collections.api.item.ItemResolver;
import gg.auroramc.collections.collection.TypeId;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.inventory.ItemStack;

public class MMOItemResolver implements ItemResolver {
    @Override
    public boolean matches(ItemStack item) {
        return NBTItem.get(item).hasType();
    }

    @Override
    public TypeId resolveId(ItemStack item) {
        var nbtItem = NBTItem.get(item);
        return new TypeId("mmoitems", nbtItem.getType() + ":" + nbtItem.getString("MMOITEMS_ITEM_ID"));
    }
}
