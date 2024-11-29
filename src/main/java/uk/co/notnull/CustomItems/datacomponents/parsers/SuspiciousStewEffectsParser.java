package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

@SuppressWarnings("UnstableApiUsage")
public class SuspiciousStewEffectsParser extends DataComponentTypeParser<ConfigurationSection, SuspiciousStewEffects> {
	private static final RegistryLookupParser<PotionEffectType> registryParser =
			new RegistryLookupParser<>(RegistryKey.MOB_EFFECT);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected SuspiciousStewEffects doParse(ConfigurationSection value) {
		SuspiciousStewEffects.Builder builder = SuspiciousStewEffects.suspiciousStewEffects();

		value.getValues(false).forEach((k, v) -> {
			PotionEffectType type = registryParser.parse(k);
			builder.add(SuspiciousEffectEntry.create(type, requiredField(k, DataComponentParsers.INT, value)));
		});

		return builder.build();
	}
}
