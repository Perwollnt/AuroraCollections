package gg.auroramc.collections.config;

import gg.auroramc.aurora.api.config.AuroraConfig;
import gg.auroramc.aurora.api.config.decorators.IgnoreField;
import gg.auroramc.aurora.api.config.premade.ConcreteMatcherConfig;
import gg.auroramc.aurora.api.config.premade.IntervalMatcherConfig;
import gg.auroramc.aurora.api.config.premade.ItemConfig;
import gg.auroramc.aurora.api.item.TypeId;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CollectionConfig extends AuroraConfig {
    private Set<String> triggers;
    private Set<String> types;
    private String name;
    private String menuTitle;
    private List<Integer> requirements;
    private Boolean useGlobalLevelMatchers;
    private Map<String, IntervalMatcherConfig> levelMatchers;
    private Map<String, ConcreteMatcherConfig> customLevels;
    private ItemConfig menuItem;

    @IgnoreField
    private Set<TypeId> parsedTypes;

    @IgnoreField
    private Set<String> parsedTriggers;

    @Getter
    public static final class CustomLevel {
        private ConfigurationSection rewards;
    }

    public CollectionConfig(File file) {
        super(file);
    }

    @Override
    public void load() {
        super.load();
        parsedTypes = types.stream().map(TypeId::fromDefault).collect(Collectors.toSet());
        parsedTriggers = triggers.stream().map(String::toUpperCase).collect(Collectors.toSet());
    }
}
