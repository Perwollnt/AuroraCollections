package gg.auroramc.collections.hooks;

import gg.auroramc.collections.hooks.auraskills.AuraSkillsHook;
import gg.auroramc.collections.hooks.auroralevels.AuroraLevelsHook;
import gg.auroramc.collections.hooks.luckperms.LuckPermsHook;
import gg.auroramc.collections.hooks.mythic.MythicHook;
import gg.auroramc.collections.hooks.worldguard.WorldGuardHook;
import lombok.Getter;

@Getter
public enum Hooks {
    AURORA_LEVELS(AuroraLevelsHook.class, "AuroraLevels"),
    AURA_SKILLS(AuraSkillsHook.class, "AuraSkills"),
    LUCK_PERMS(LuckPermsHook.class, "LuckPerms"),
    MYTHIC_MOBS(MythicHook.class, "MythicMobs"),
    WORLD_GUARD(WorldGuardHook.class, "WorldGuard");

    private final Class<? extends Hook> clazz;
    private final String plugin;

    Hooks(Class<? extends Hook> clazz, String plugin) {
        this.clazz = clazz;
        this.plugin = plugin;
    }
}
