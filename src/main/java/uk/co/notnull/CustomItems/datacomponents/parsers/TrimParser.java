package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

@SuppressWarnings("UnstableApiUsage")
public class TrimParser extends DataComponentTypeParser<ConfigurationSection, ItemArmorTrim> {
	private static final RegistryLookupParser<TrimMaterial> materialParser =
			new RegistryLookupParser<>(RegistryKey.TRIM_MATERIAL);
	private static final RegistryLookupParser<TrimPattern> patternParser =
			new RegistryLookupParser<>(RegistryKey.TRIM_PATTERN);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected ItemArmorTrim doParse(ConfigurationSection value) {
		TrimMaterial material = requiredField("material", materialParser, value);
		TrimPattern pattern = requiredField("pattern", patternParser, value);

		ItemArmorTrim.Builder builder = ItemArmorTrim.itemArmorTrim(new ArmorTrim(material, pattern));

		return builder.build();
	}
}
