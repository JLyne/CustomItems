package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

@SuppressWarnings("UnstableApiUsage")
public class EnchantmentsParser extends DataComponentTypeParser<ConfigurationSection, ItemEnchantments> {
	private static final RegistryLookupParser<Enchantment> registryParser =
			new RegistryLookupParser<>(RegistryKey.ENCHANTMENT);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected ItemEnchantments doParse(ConfigurationSection value) {
		ItemEnchantments.Builder builder = ItemEnchantments.itemEnchantments();

		value.getValues(false).forEach((k, v) ->
											   builder.add(registryParser.parse(k),
														   requiredField(k, DataComponentParsers.INT, value)));

		return builder.build();
	}
}
