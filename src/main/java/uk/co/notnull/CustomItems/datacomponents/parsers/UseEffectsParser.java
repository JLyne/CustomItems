package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.UseEffects;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class UseEffectsParser extends DataComponentTypeParser<ConfigurationSection, UseEffects> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected UseEffects doParse(ConfigurationSection value) {
		boolean canSprint = optionalField("can_sprint", DataComponentParsers.BOOLEAN, value, false);
		float speedMultiplier = optionalField("speed_multiplier", DataComponentParsers.FLOAT, value, 0.2f);
		boolean interactVibrations = optionalField("interact_vibrations", DataComponentParsers.BOOLEAN, value, true);

		return UseEffects.useEffects()
				.canSprint(canSprint)
				.interactVibrations(interactVibrations)
				.speedMultiplier(speedMultiplier)
				.build();
	}
}
