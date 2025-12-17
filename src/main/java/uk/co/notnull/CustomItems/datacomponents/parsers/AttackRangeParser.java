package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class AttackRangeParser extends DataComponentTypeParser<ConfigurationSection, AttackRange> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected AttackRange doParse(ConfigurationSection value) {
		Float minReach = optionalField("min_reach", DataComponentParsers.FLOAT, value);
		Float maxReach = optionalField("max_reach", DataComponentParsers.FLOAT, value);
		Float hitboxMargin = optionalField("hitbox_margin", DataComponentParsers.FLOAT, value);
		Float mobFactor = optionalField("mob_factor", DataComponentParsers.FLOAT, value);
		Float minCreativeReach = optionalField("min_creative_reach", DataComponentParsers.FLOAT, value);
		Float maxCreativeReach = optionalField("max_creative_reach", DataComponentParsers.FLOAT, value);

		AttackRange.Builder builder = AttackRange.attackRange();

		if (minReach != null) {
			builder.minReach(minReach);
		}

		if (maxReach != null) {
			builder.maxReach(maxReach);
		}

		if (hitboxMargin != null) {
			builder.hitboxMargin(hitboxMargin);
		}

		if (mobFactor != null) {
			builder.mobFactor(mobFactor);
		}

		if (minCreativeReach != null) {
			builder.minCreativeReach(minCreativeReach);
		}

		if (maxCreativeReach != null) {
			builder.maxCreativeReach(maxCreativeReach);
		}

		return builder.build();
	}
}
