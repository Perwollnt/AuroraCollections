package gg.auroramc.collections.reward;

import gg.auroramc.aurora.api.command.CommandDispatcher;
import gg.auroramc.aurora.api.expression.BooleanExpression;
import gg.auroramc.aurora.api.message.Placeholder;
import gg.auroramc.collections.AuroraCollections;
import gg.auroramc.collections.api.reward.AbstractReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandReward extends AbstractReward {
    private String command;
    private String correctionExpression;

    @Override
    public void execute(Player player, long level, List<Placeholder<?>> placeholders) {
        if (command == null) return;
        CommandDispatcher.dispatch(player, command, placeholders);
    }

    @Override
    public void init(ConfigurationSection args) {
        super.init(args);
        command = args.getString("command", null);
        correctionExpression = args.getString("correction-condition");

        if (command == null) {
            AuroraCollections.logger().warning("CommandReward has no command key");
        }
    }

    public boolean shouldBeCorrected(Player player, long level) {
        if (correctionExpression == null || command == null) return false;
        return BooleanExpression.eval(player, correctionExpression, Placeholder.of("{level}", level));
    }
}
