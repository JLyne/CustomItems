package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.EnumParser;

@SuppressWarnings("UnstableApiUsage")
public class SwingAnimationParser extends DataComponentTypeParser<ConfigurationSection, SwingAnimation> {
	EnumParser<SwingAnimation.Animation> animationParser = new EnumParser<>(SwingAnimation.Animation.class);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected SwingAnimation doParse(ConfigurationSection value) {
		SwingAnimation.Animation type = optionalField("type", animationParser, value);
		Integer duration = optionalField("duration", DataComponentParsers.INT, value);

		SwingAnimation.Builder builder = SwingAnimation.swingAnimation();

		if(type != null) {
			builder.type(type);
		}

		if(duration != null) {
			builder.duration(duration);
		}

		return builder.build();
	}
}
