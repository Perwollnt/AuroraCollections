package gg.auroramc.collections.api.reward;

import com.google.common.collect.Maps;
import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.collection.CollectionManager;
import org.bukkit.entity.Player;

import java.util.Map;

public class RewardAutoCorrector {
    private final static Map<NamespacedId, RewardCorrector> correctors = Maps.newConcurrentMap();

    public static void registerCorrector(NamespacedId id, RewardCorrector corrector) {
        correctors.put(id, corrector);
    }

    public static void correctRewards(CollectionManager manager, Player player) {
        for (var entry : correctors.entrySet()) {
            entry.getValue().correctRewards(manager, player);
        }
    }
}
