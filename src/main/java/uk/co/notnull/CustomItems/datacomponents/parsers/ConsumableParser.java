package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.EnumParser;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ConsumableParser extends DataComponentTypeParser<ConfigurationSection, Consumable> {
	private static final EnumParser<ItemUseAnimation> animationParser = new EnumParser<>(ItemUseAnimation.class);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Consumable doParse(ConfigurationSection value) {
		Consumable.Builder builder = Consumable.consumable();

		Float consumeSeconds = optionalField("consume_seconds", DataComponentParsers.FLOAT, value);
		ItemUseAnimation animation = optionalField("animation", animationParser, value);
		NamespacedKey sound = optionalField("sound", DataComponentParsers.NAMESPACED_KEY, value);
		Boolean hasConsumeParticles = optionalField("has_consume_particles", DataComponentParsers.BOOLEAN, value);
		List<ConsumeEffect> onConsumeEffects = optionalListField("on_comsume_effects",
																 DataComponentParsers.CONSUME_EFFECT, value);

		if(consumeSeconds != null) {
			builder.consumeSeconds(consumeSeconds);
		}

		if(animation != null) {
			builder.animation(animation);
		}

		if(sound != null) {
			builder.sound(sound);
		}

		if(hasConsumeParticles != null) {
			builder.hasConsumeParticles(hasConsumeParticles);
		}

		if(onConsumeEffects != null) {
			builder.addEffects(onConsumeEffects);
		}

		return builder.build();
	}
}
