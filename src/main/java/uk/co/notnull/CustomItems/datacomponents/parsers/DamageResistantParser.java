package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class DamageResistantParser extends DataComponentTypeParser<ConfigurationSection, DamageResistant> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected DamageResistant doParse(ConfigurationSection value) {
		NamespacedKey types = requiredField("types", DataComponentParsers.NAMESPACED_KEY, value);

		return DamageResistant.damageResistant(TagKey.create(RegistryKey.DAMAGE_TYPE, types));
	}
}
