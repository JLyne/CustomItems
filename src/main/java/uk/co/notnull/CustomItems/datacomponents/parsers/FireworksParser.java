package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Fireworks;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class FireworksParser extends DataComponentTypeParser<ConfigurationSection, Fireworks> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Fireworks doParse(ConfigurationSection value) {
		Fireworks.Builder builder = Fireworks.fireworks();
		List<FireworkEffect> explosions =
				optionalListField("explosions", DataComponentParsers.FIREWORK_EXPLOSION, value);
		Integer flightDuration = optionalField("flight_duration", DataComponentParsers.INT, value);

		if(explosions != null) {
			builder.addEffects(explosions);
		}

		if(flightDuration != null) {
			builder.flightDuration(flightDuration);
		}

		return builder.build();
	}
}
