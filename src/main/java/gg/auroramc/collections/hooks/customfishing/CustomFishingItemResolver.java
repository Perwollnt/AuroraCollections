package gg.auroramc.collections.hooks.customfishing;

import gg.auroramc.collections.api.item.ItemResolver;
import gg.auroramc.collections.collection.TypeId;
import net.momirealms.customfishing.api.CustomFishingPlugin;
import org.bukkit.inventory.ItemStack;

public class CustomFishingItemResolver implements ItemResolver {
    @Override
    public boolean matches(ItemStack item) {
        return CustomFishingPlugin.get().getItemManager().isCustomFishingItem(item);
    }

    @Override
    public TypeId resolveId(ItemStack item) {
        return new TypeId("customfishing", CustomFishingPlugin.get().getItemManager().getCustomFishingItemID(item));
    }
}
