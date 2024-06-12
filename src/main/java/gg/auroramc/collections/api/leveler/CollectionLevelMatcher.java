package gg.auroramc.collections.api.leveler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.reward.LevelReward;
import gg.auroramc.collections.api.reward.RewardFactory;
import gg.auroramc.collections.config.CollectionConfig;
import gg.auroramc.collections.config.Config;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

@Getter
public class CollectionLevelMatcher {
    private final AuroraCollections plugin;
    private final List<Matcher> matchers = Lists.newCopyOnWriteArrayList();
    private final Map<Long, Matcher> customMatchers = Maps.newConcurrentMap();

    public CollectionLevelMatcher(AuroraCollections plugin) {
        this.plugin = plugin;
    }

    public void reload(CollectionConfig config) {
        var globalConfig = plugin.getConfigManager().getConfig();

        matchers.clear();
        customMatchers.clear();

        Map<String, Config.LevelMatcherConfig> collectionMatchers = new HashMap<>();

        if (config.getUseGlobalLevelMatchers()) {
            collectionMatchers.putAll(globalConfig.getGlobalLevelMatchers());
            collectionMatchers.putAll(config.getLevelMatchers());
        } else {
            collectionMatchers.putAll(config.getLevelMatchers());
        }

        matchers.addAll(collectionMatchers.values().stream()
                .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
                .map((m) -> new Matcher(m.getInterval(), m.getPriority(), createRewards(m.getRewards())))
                .toList());

        for (var cLevel : config.getCustomLevels().entrySet()) {
            customMatchers.put(cLevel.getKey().longValue(), new Matcher(0, 0, createRewards(cLevel.getValue().getRewards())));
        }
    }

    private List<LevelReward> createRewards(ConfigurationSection rewards) {
        if (rewards == null) return new ArrayList<>();

        return rewards.getKeys(false).stream().map(rewards::getConfigurationSection)
                .map(RewardFactory::createReward)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Get the best matcher for the given level
     *
     * @param level level to get the matcher for
     * @return the best matcher for the given level
     */
    public Matcher getBestMatcher(long level) {
        if (customMatchers.containsKey(level)) {
            return customMatchers.get(level);
        }

        for (var matcher : matchers) {
            if (matcher.matches(level)) {
                return matcher;
            }
        }

        return null;
    }
}
