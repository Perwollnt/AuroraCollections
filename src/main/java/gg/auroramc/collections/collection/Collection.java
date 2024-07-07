package gg.auroramc.collections.collection;

import gg.auroramc.aurora.api.AuroraAPI;
import gg.auroramc.aurora.api.config.premade.IntervalMatcherConfig;
import gg.auroramc.aurora.api.item.TypeId;
import gg.auroramc.aurora.api.levels.MatcherManager;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.aurora.api.message.Text;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.data.CollectionData;
import gg.auroramc.collections.api.event.CollectionLevelUpEvent;
import gg.auroramc.collections.config.CollectionConfig;
import gg.auroramc.collections.util.RomanNumber;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Collection {
    private final CollectionConfig config;
    private final String id;
    private final String category;
    private final MatcherManager levelMatcher;
    private final AuroraCollections plugin;

    public Collection(AuroraCollections plugin, CollectionConfig config, String category, String id) {
        this.plugin = plugin;
        this.config = config;
        this.category = category;
        this.id = id;
        this.levelMatcher = new MatcherManager(plugin.getCollectionManager().getRewardFactory());

        var globalConfig = plugin.getConfigManager().getConfig();

        Map<String, IntervalMatcherConfig> collectionMatchers = new LinkedHashMap<>();

        if(config.getUseGlobalLevelMatchers()) {
            collectionMatchers.putAll(globalConfig.getGlobalLevelMatchers());
            collectionMatchers.putAll(config.getLevelMatchers());
        } else {
            collectionMatchers.putAll(config.getLevelMatchers());
        }

        levelMatcher.reload(collectionMatchers, config.getCustomLevels());
    }

    public int getPlayerLevel(Player player) {
        var data = AuroraAPI.getUserManager().getUser(player).getData(CollectionData.class);
        var progress = data.getCollectionCount(category, id);
        var requirements = config.getRequirements();

        for (int i = requirements.size() - 1; i >= 0; i--) {
            if (progress > requirements.get(i)) {
                return i + 1;
            }
        }
        return 0;
    }

    public long getRequiredAmount(long level) {
        return config.getRequirements().size() < level ? config.getRequirements().getLast() : config.getRequirements().get((int) level - 1);
    }

    public synchronized void progress(Player player, @Nullable TypeId type, int amount) {
        if (type != null && !config.getParsedTypes().contains(type)) {
            return;
        }

        if (!AuroraAPI.getUser(player.getUniqueId()).isLoaded()) return;

        var oldLevel = getPlayerLevel(player);
        var data = AuroraAPI.getUserManager().getUser(player).getData(CollectionData.class);
        data.incrementCollectionCount(category, id, amount);
        var newLevel = getPlayerLevel(player);

        if (newLevel <= oldLevel) {
            return;
        }

        var mainConfig = plugin.getConfigManager().getConfig();

        for (int i = oldLevel + 1; i <= newLevel; i++) {
            var matcher = levelMatcher.getBestMatcher(newLevel);
            var placeholders = getPlaceholders(player, oldLevel, newLevel);


            var text = Component.text();
            var messageLines = mainConfig.getLevelUpMessage().getMessage();

            var rewards = matcher.computeRewards(i);

            for (var line : messageLines) {
                if (line.equals("component:rewards")) {

                    if (!rewards.isEmpty()) {
                        text.append(Text.component(player, mainConfig.getDisplayComponents().get("rewards").getTitle(), placeholders));
                    }
                    for (var reward : rewards) {
                        text.append(Component.newline());
                        var display = mainConfig.getDisplayComponents().get("rewards").getLine().replace("{reward}", reward.getDisplay(player, placeholders));
                        text.append(Text.component(player, display, placeholders));
                    }
                } else {
                    text.append(Text.component(player, line, placeholders));
                }

                if (!line.equals(messageLines.getLast())) text.append(Component.newline());
            }


            int finalI = i;

            Bukkit.getGlobalRegionScheduler().run(plugin,
                    (task) -> {
                        if (mainConfig.getLevelUpSound().getEnabled()) {
                            var sound = mainConfig.getLevelUpSound();
                            player.playSound(player.getLocation(), Sound.valueOf(sound.getSound().toUpperCase()), sound.getVolume(), sound.getPitch());
                        }

                        if (mainConfig.getLevelUpMessage().getEnabled()) {
                            player.sendMessage(text);
                        }

                        for (var reward : rewards) {
                            reward.execute(player, newLevel, placeholders);
                        }
                        Bukkit.getPluginManager().callEvent(new CollectionLevelUpEvent(player, this, finalI));
                    });
        }
    }

    private List<Placeholder<?>> getPlaceholders(Player player, long oldLevel, long newLevel) {
        var requirement = getRequiredAmount(newLevel);

        var pConfig = plugin.getConfigManager().getCollectionMenuConfig();
        var data = AuroraAPI.getUser(player.getUniqueId()).getData(CollectionData.class);
        var currentProgress = data.getCollectionCount(category, id);
        var bar = pConfig.getProgressBar();
        var pcs = bar.getLength();
        var completedPercent = Math.min((double) currentProgress / requirement, 1);
        var completedPcs = ((Double) Math.floor(pcs * completedPercent)).intValue();
        var remainingPcs = pcs - completedPcs;

        var config = plugin.getConfigManager().getCollectionMenuConfig();
        var roman = config.getForceRomanNumerals();

        var oldLevel2 = roman ? RomanNumber.toRoman(oldLevel) : String.valueOf(oldLevel);
        var newLevel2 = roman ? RomanNumber.toRoman(newLevel) : String.valueOf(newLevel);

        return List.of(
                Placeholder.of("{player}", player.getName()),
                Placeholder.of("{prev_level}", oldLevel2),
                Placeholder.of("{prev_level_raw}", oldLevel),
                Placeholder.of("{prev_level_formatted}", roman ? oldLevel2 : AuroraAPI.formatNumber(oldLevel)),
                Placeholder.of("{prev_level_roman}", RomanNumber.toRoman(oldLevel)),
                Placeholder.of("{level}", newLevel2),
                Placeholder.of("{level_raw}", newLevel),
                Placeholder.of("{level_roman}", RomanNumber.toRoman(newLevel)),
                Placeholder.of("{level_formatted}", roman ? newLevel2 : AuroraAPI.formatNumber(newLevel)),
                Placeholder.of("{collection}", id),
                Placeholder.of("{collection_name}", this.config.getName()),
                Placeholder.of("{category}", category),
                Placeholder.of("{category_name}", plugin.getConfigManager().getCategoriesConfig().getCategories().get(category)),
                Placeholder.of("{progressbar}", bar.getFilledCharacter().repeat(completedPcs) + bar.getUnfilledCharacter().repeat(remainingPcs) + "&r"),
                Placeholder.of("{progress_percent}", Math.round(completedPercent * 100)),
                Placeholder.of("{current}", currentProgress),
                Placeholder.of("{current_formatted}", AuroraAPI.formatNumber(currentProgress)),
                Placeholder.of("{required}", requirement),
                Placeholder.of("{required_formatted}", AuroraAPI.formatNumber(requirement)),
                Placeholder.of("{total}", data.getCollectionCount(category, id)),
                Placeholder.of("{total_formatted}", AuroraAPI.formatNumber(data.getCollectionCount(category, id)))
        );
    }

    public List<Placeholder<?>> getPlaceholders(Player player, long level) {
        level = Math.max(1, level);
        return getPlaceholders(player, level - 1, level);
    }

    public void progress(Player player, TypeId type) {
        progress(player, type, 1);
    }
}
