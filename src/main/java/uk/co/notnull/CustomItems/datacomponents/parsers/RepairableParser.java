package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryKeySetParser;

@SuppressWarnings("UnstableApiUsage")
public class RepairableParser extends DataComponentTypeParser<ConfigurationSection, Repairable> {
	private static final RegistryKeySetParser<ItemType> keySetParser =
			new RegistryKeySetParser<>(RegistryKey.ITEM, true);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Repairable doParse(ConfigurationSection value) {
		return Repairable.repairable(requiredField("items", keySetParser, value));
	}
}
