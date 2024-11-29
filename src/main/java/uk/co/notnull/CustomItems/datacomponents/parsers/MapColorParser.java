package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.MapItemColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class MapColorParser extends DataComponentTypeParser<ConfigurationSection, MapItemColor> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected MapItemColor doParse(ConfigurationSection value) {
		return MapItemColor.mapItemColor().color(DataComponentParsers.COLOR.parse(value)).build();
	}
}
