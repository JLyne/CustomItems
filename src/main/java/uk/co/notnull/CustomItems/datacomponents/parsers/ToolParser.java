package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import net.kyori.adventure.util.TriState;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryKeySetParser;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ToolParser extends DataComponentTypeParser<ConfigurationSection, Tool> {
	private static final RuleParser ruleParser = new RuleParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Tool doParse(ConfigurationSection value) {
		Float defaultMiningSpeed = optionalField("default_mining_speed", DataComponentParsers.FLOAT, value);
		Integer damagePerBlock = optionalField("damage_per_block", DataComponentParsers.INT, value);
		Boolean canDestroyBlocksInCreative = optionalField("can_destroy_blocks_in_creative", DataComponentParsers.BOOLEAN, value);
		List<Tool.Rule> rules = requiredListField("rules", ruleParser, value);

		Tool.Builder builder = Tool.tool();

		if(defaultMiningSpeed != null) {
			builder.defaultMiningSpeed(defaultMiningSpeed);
		}

		if(damagePerBlock != null) {
			builder.damagePerBlock(damagePerBlock);
		}

		if(canDestroyBlocksInCreative != null) {
			builder.canDestroyBlocksInCreative(canDestroyBlocksInCreative);
		}

		builder.addRules(rules);

		return builder.build();
	}

	private static class RuleParser extends DataComponentTypeParser<ConfigurationSection, Tool.Rule> {
		private static final RegistryKeySetParser<BlockType> keySetParser =
				new RegistryKeySetParser<>(RegistryKey.BLOCK, true);

		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected Tool.Rule doParse(ConfigurationSection value) {
			RegistryKeySet<BlockType> keySet = requiredField("blocks", keySetParser, value);
			Float speed = optionalField("speed", DataComponentParsers.FLOAT, value);
			Boolean correctForDrops = optionalField("correct_for_drops", DataComponentParsers.BOOLEAN, value);

			return Tool.rule(keySet, speed, TriState.byBoolean(correctForDrops));
		}
	}
}
