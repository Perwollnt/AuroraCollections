package gg.auroramc.collections.hooks.luckperms;

import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.reward.RewardAutoCorrector;
import gg.auroramc.collections.api.reward.RewardFactory;
import gg.auroramc.collections.hooks.Hook;

public class LuckPermsHook implements Hook {
    @Override
    public void hook(AuroraCollections plugin) {
        RewardFactory.registerRewardType(NamespacedId.fromDefault("permission"), PermissionReward.class);
        RewardAutoCorrector.registerCorrector(NamespacedId.fromDefault("permission"), new PermissionCorrector());
        AuroraCollections.logger().info("Hooked into LuckPerms for permission rewards with reward type: 'permission'. Auto reward corrector for permissions is registered.");
    }
}
