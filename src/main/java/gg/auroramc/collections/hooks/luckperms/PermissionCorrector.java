package gg.auroramc.collections.hooks.luckperms;

import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.reward.RewardCorrector;
import gg.auroramc.collections.collection.CollectionManager;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.util.Tristate;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class PermissionCorrector implements RewardCorrector {
    @Override
    public void correctRewards(CollectionManager manager, Player player) {
        CompletableFuture.runAsync(() -> {
            for (var collection : manager.getAllCollections()) {
                var level = collection.getPlayerLevel(player);

                for (long i = 1; i < level + 1; i++) {
                    var matcher = collection.getLevelMatcher().getBestMatcher(i);
                    if (matcher == null) continue;
                    var placeholders = collection.getPlaceholders(player, i);
                    for (var reward : matcher.rewards()) {
                        if (reward instanceof PermissionReward permissionReward) {
                            if (permissionReward.getPermission() == null) continue;
                            var node = permissionReward.buildNode(player, placeholders);
                            var hasPermission = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId())
                                    .data().contains(node, NodeEqualityPredicate.EXACT);

                            if (hasPermission.equals(Tristate.UNDEFINED)) {
                                AuroraCollections.logger().debug("Permission " + node.getKey() + " is undefined for player " + player.getName());
                                permissionReward.execute(player, i, placeholders);
                            }
                        }
                    }
                }
            }
        });
    }
}
