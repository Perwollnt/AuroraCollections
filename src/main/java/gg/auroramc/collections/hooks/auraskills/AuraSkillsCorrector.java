package gg.auroramc.collections.hooks.auraskills;

import com.google.common.collect.Maps;
import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.stat.Stat;
import dev.aurelium.auraskills.api.stat.StatModifier;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.reward.RewardCorrector;
import gg.auroramc.collections.collection.CollectionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AuraSkillsCorrector implements RewardCorrector {
    private final AuroraCollections plugin;

    public AuraSkillsCorrector(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    @Override
    public void correctRewards(CollectionManager manager, Player player) {
        CompletableFuture.runAsync(() -> {
            Map<Stat, Double> statMap = Maps.newHashMap();

            // Reset all stat modifiers first
            for (var stat : AuraSkillsApi.get().getGlobalRegistry().getStats()) {
                statMap.put(stat, 0.0);
            }

            // Gather new stat modifiers
            for(var collection : manager.getAllCollections()) {
                var level = collection.getPlayerLevel(player);

                for (long i = 1; i < level + 1; i++) {
                    var matcher = collection.getLevelMatcher().getBestMatcher(i);
                    if (matcher == null) continue;
                    var placeholders = collection.getPlaceholders(player, i);
                    for (var reward : matcher.rewards()) {
                        if (reward instanceof AuraSkillsStatReward statReward) {
                            statMap.merge(statReward.getStat(), statReward.getValue(placeholders), Double::sum);
                        }
                    }
                }
            }

            Bukkit.getGlobalRegionScheduler().run(plugin, (task) -> {
                for (var entry : statMap.entrySet()) {
                    var statKey = AuraSkillsStatReward.getAURA_SKILLS_STAT() + entry.getKey().getId().toString();
                    var user = AuraSkillsApi.get().getUser(player.getUniqueId());

                    var oldModifier = user.getStatModifier(statKey);

                    if (oldModifier == null) {
                        if (entry.getValue() > 0) {
                            user.addStatModifier(new StatModifier(statKey, entry.getKey(), entry.getValue()));
                        }
                    } else if (entry.getValue() <= 0) {
                        user.removeStatModifier(statKey);
                    } else if (entry.getValue() != oldModifier.value()) {
                        user.addStatModifier(new StatModifier(statKey, entry.getKey(), entry.getValue()));
                    }
                }
            });
        });
    }
}
