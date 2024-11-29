package uk.co.notnull.CustomItems.datacomponents.parsers;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

public class ShowInTooltipParser extends DataComponentTypeParser<ConfigurationSection, Boolean> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Boolean doParse(ConfigurationSection value) {
		return optionalField("show_in_tooltip", DataComponentParsers.BOOLEAN, value, true);
	}
}
