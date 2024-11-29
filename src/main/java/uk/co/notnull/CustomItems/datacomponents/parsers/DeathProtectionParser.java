package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DeathProtectionParser extends DataComponentTypeParser<ConfigurationSection, DeathProtection> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected DeathProtection doParse(ConfigurationSection value) {
		List<ConsumeEffect> effects = optionalListField("death_effects", DataComponentParsers.CONSUME_EFFECT, value);
		DeathProtection.Builder builder = DeathProtection.deathProtection();

		if(effects != null) {
			builder.addEffects(effects);
		}

		return builder.build();
	}
}
