package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryKeySetParser;

@SuppressWarnings("UnstableApiUsage")
public class DamageResistantParser extends DataComponentTypeParser<ConfigurationSection, DamageResistant> {
	private static final RegistryKeySetParser<DamageType> keySetParser =
			new RegistryKeySetParser<>(RegistryKey.DAMAGE_TYPE, true);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected DamageResistant doParse(ConfigurationSection value) {
		RegistryKeySet<DamageType> types = requiredField("types", keySetParser, value);

		return DamageResistant.damageResistant(types);
	}
}
