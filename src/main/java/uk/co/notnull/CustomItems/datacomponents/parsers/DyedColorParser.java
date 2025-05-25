package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.DyedItemColor;
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
		return DyedItemColor.dyedItemColor(DataComponentParsers.COLOR.parse(value));
	}
}
