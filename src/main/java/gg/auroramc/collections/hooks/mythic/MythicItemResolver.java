package gg.auroramc.collections.hooks.mythic;

import gg.auroramc.collections.api.item.ItemResolver;
import gg.auroramc.collections.collection.TypeId;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;

public class MythicItemResolver implements ItemResolver {
    @Override
    public boolean matches(ItemStack item) {
        return MythicBukkit.inst().getItemManager().isMythicItem(item);
    }

    @Override
    public TypeId resolveId(ItemStack item) {
        return new TypeId("mythicmobs", MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item));
    }
}
