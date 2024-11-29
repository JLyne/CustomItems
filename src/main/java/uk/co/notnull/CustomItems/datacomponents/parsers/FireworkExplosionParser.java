package uk.co.notnull.CustomItems.datacomponents.parsers;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;

public class FireworkExplosionParser extends DataComponentTypeParser<ConfigurationSection, FireworkEffect> {
	private static final ShapeParser shapeParser = new ShapeParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected FireworkEffect doParse(ConfigurationSection value) {
		FireworkEffect.Type shape = requiredField("shape", shapeParser, value);
		List<Color> colors = optionalListField("colors", DataComponentParsers.COLOR, value);
		List<Color> fadeColors = optionalListField("fade_colors", DataComponentParsers.COLOR, value);
		Boolean hasTwinkle = optionalField("has_twinkle", DataComponentParsers.BOOLEAN, value);
		Boolean hasTrail = optionalField("has_trail", DataComponentParsers.BOOLEAN, value);
		FireworkEffect.Builder builder = FireworkEffect.builder();

		builder.with(shape);

		if(colors != null) {
			builder.withColor(colors);
		}

		if(fadeColors != null) {
			builder.withFade(fadeColors);
		}

		if(hasTwinkle != null) {
			builder.flicker(hasTwinkle);
		}

		if(hasTrail != null) {
			builder.trail(hasTrail);
		}

		return builder.build();
	}

	private static class ShapeParser extends DataComponentTypeParser<String, FireworkEffect.Type> {
		@Override
		protected @NotNull Class<String> getConfigType() {
			return String.class;
		}

		@Override
		protected FireworkEffect.Type doParse(String value) {
			return FireworkEffect.Type.NAMES.valueOrThrow(value);
		}
	}
}
