package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.SeededContainerLoot;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class ContainerLootParser extends DataComponentTypeParser<ConfigurationSection, SeededContainerLoot> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected SeededContainerLoot doParse(ConfigurationSection value) {
		NamespacedKey lootTable = requiredField("loot_table", DataComponentParsers.NAMESPACED_KEY, value);
		Long seed = optionalField("seed", DataComponentParsers.LONG, value);

		SeededContainerLoot.Builder builder = SeededContainerLoot.seededContainerLoot(lootTable);

		if(seed != null) {
			builder.seed(seed);
		}

		return builder.build();
	}
}
