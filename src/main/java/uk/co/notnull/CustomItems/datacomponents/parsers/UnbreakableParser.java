package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Unbreakable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class UnbreakableParser extends DataComponentTypeParser<ConfigurationSection, Unbreakable> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Unbreakable doParse(ConfigurationSection value) {
		return Unbreakable.unbreakable(DataComponentParsers.SHOW_IN_TOOLTIP.parse(value));
	}
}
