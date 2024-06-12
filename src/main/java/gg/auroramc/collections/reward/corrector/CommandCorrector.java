package gg.auroramc.collections.reward.corrector;

import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.reward.RewardCorrector;
import gg.auroramc.collections.collection.CollectionManager;
import gg.auroramc.collections.reward.CommandReward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommandCorrector implements RewardCorrector {

    private final AuroraCollections plugin;

    public CommandCorrector(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    private static record CommandPair(CommandReward reward, List<Placeholder<?>> placeholders) {}

    @Override
    public void correctRewards(CollectionManager manager, Player player) {
        CompletableFuture.runAsync(() -> {

            final var rewards = new HashMap<Long, CommandPair>();

            for(var collection : manager.getAllCollections()) {
                var level = collection.getPlayerLevel(player);

                for (long i = 1; i < level + 1; i++) {
                    var matcher = collection.getLevelMatcher().getBestMatcher(i);
                    if (matcher == null) continue;

                    for (var reward : matcher.rewards()) {
                        if (reward instanceof CommandReward commandReward) {
                            if (commandReward.shouldBeCorrected(player, i)) {
                                rewards.put(i, new CommandPair(commandReward, collection.getPlaceholders(player, i)));
                            }
                        }
                    }
                }
            }

            if (rewards.isEmpty()) return;

            Bukkit.getGlobalRegionScheduler().run(plugin, (task) -> {
                rewards.forEach((lvl, reward) -> {
                    if (!player.isOnline()) return;
                    reward.reward().execute(player, lvl, reward.placeholders());
                });
                AuroraCollections.logger().debug("Corrected %d command rewards for player %s".formatted(rewards.size(), player.getName()));
            });
        });
    }
}
