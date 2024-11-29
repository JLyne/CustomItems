package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class ColorParser extends DataComponentTypeParser<ConfigurationSection, Color> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Color doParse(ConfigurationSection value) {
		int red = requiredField("red", DataComponentParsers.INT, value);
		int blue = requiredField("blue", DataComponentParsers.INT, value);
		int green = requiredField("green", DataComponentParsers.INT, value);
		int alpha = requiredField("alpha", DataComponentParsers.INT, value);

		return Color.fromARGB(alpha, red, blue, green);
	}
}
