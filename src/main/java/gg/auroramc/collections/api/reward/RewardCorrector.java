package gg.auroramc.collections.api.reward;

import gg.auroramc.collections.collection.CollectionManager;
import org.bukkit.entity.Player;

public interface RewardCorrector {
    void correctRewards(CollectionManager manager, Player player);
}
