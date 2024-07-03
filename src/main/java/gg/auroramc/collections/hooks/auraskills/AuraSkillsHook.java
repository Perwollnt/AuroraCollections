package gg.auroramc.collections.hooks.auraskills;

import gg.auroramc.aurora.api.util.NamespacedId;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.hooks.Hook;
import org.bukkit.Bukkit;

public class AuraSkillsHook implements Hook {
    @Override
    public void hook(AuroraCollections plugin) {

        plugin.getCollectionManager().getRewardFactory()
                .registerRewardType(NamespacedId.fromDefault("auraskills_stat"), AuraSkillsStatReward.class);

        plugin.getCollectionManager().getRewardAutoCorrector()
                .registerCorrector(NamespacedId.fromDefault("auraskills_stat"), new AuraSkillsCorrector(plugin));

        Bukkit.getPluginManager().registerEvents(new AuraSkillsListener(plugin), plugin);

        AuroraCollections.logger().info("Hooked into AuraSkills for handling extra loot drops");
        AuroraCollections.logger().info("Hooked into AuraSkills for stat rewards with reward type: 'auraskills_stat'. Auto reward corrector for stats is registered.");
    }
}
