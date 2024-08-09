package gg.auroramc.collections.collection;

import gg.auroramc.aurora.api.reward.Reward;

import java.util.List;

public record CategoryReward(double percentage, List<Reward> rewards) {
}
