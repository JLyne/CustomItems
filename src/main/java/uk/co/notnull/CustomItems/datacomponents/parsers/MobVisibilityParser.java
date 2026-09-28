package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.MobVisibility;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryKeySetParser;

@SuppressWarnings("UnstableApiUsage")
public class MobVisibilityParser extends DataComponentTypeParser<ConfigurationSection, MobVisibility> {
	private static final RegistryKeySetParser<EntityType> keySetParser =
			new RegistryKeySetParser<>(RegistryKey.ENTITY_TYPE, true);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected MobVisibility doParse(ConfigurationSection value) {
		RegistryKeySet<@NotNull EntityType> targetingEntityTypes = requiredField("targeting_entity_types", keySetParser, value);
		Float visibility = requiredField("visibility", DataComponentParsers.FLOAT, value);

		return MobVisibility.mobVisibility(targetingEntityTypes, visibility);
	}
}
