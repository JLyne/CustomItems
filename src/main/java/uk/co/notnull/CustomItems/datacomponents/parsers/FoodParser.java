package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class FoodParser extends DataComponentTypeParser<ConfigurationSection, FoodProperties> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected FoodProperties doParse(ConfigurationSection value) {
		Boolean canAlwaysEat = optionalField("can_always_eat", DataComponentParsers.BOOLEAN, value);
		Integer nutrition = optionalField("nutrition", DataComponentParsers.INT, value);
		Float saturation = optionalField("saturation", DataComponentParsers.FLOAT, value);

		FoodProperties.Builder builder = FoodProperties.food();

		if(canAlwaysEat != null) {
			builder.canAlwaysEat(canAlwaysEat);
		}

		if(nutrition != null) {
			builder.nutrition(nutrition);
		}

		if(saturation != null) {
			builder.saturation(saturation);
		}

		return builder.build();
	}
}
