package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.DyedItemColor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class DyedColorParser extends DataComponentTypeParser<ConfigurationSection, DyedItemColor> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected DyedItemColor doParse(ConfigurationSection value) {
		Color color = requiredField("rgb", DataComponentParsers.COLOR, value);

		return DyedItemColor.dyedItemColor(color, DataComponentParsers.SHOW_IN_TOOLTIP.parse(value));
	}
}
