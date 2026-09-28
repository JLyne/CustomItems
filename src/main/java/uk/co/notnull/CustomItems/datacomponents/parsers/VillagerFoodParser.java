package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.VillagerFood;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

public class VillagerFoodParser extends DataComponentTypeParser<ConfigurationSection, VillagerFood> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected VillagerFood doParse(ConfigurationSection value) {
		Integer nutrition = requiredField("nutrition", DataComponentParsers.INT, value);

		return VillagerFood.villagerFood(nutrition);
	}
}
