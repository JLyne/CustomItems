package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryKeySetParser;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class ConsumeEffectListParser extends DataComponentTypeParser<ConfigurationSection, ConsumeEffect> {
	private static final PotionContentsParser.PotionEffectParser effectParser =
			new PotionContentsParser.PotionEffectParser();
	private static final RegistryKeySetParser<PotionEffectType> keySetParser =
			new RegistryKeySetParser<>(RegistryKey.MOB_EFFECT, false);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected ConsumeEffect doParse(ConfigurationSection value) {
		String type = requiredField("type", DataComponentParsers.STRING, value);

		switch(type) {
			case "apply_effects" -> {
				float probability = optionalField("probability", DataComponentParsers.FLOAT, value, 1.0f);
				List<PotionEffect> effects = requiredListField("effects", effectParser, value);

				return ConsumeEffect.applyStatusEffects(effects, probability);
			}

			case "remove_effects" -> {
				return ConsumeEffect.removeEffects(requiredField("effects", keySetParser, value));
			}

			case "clear_effects" -> {
				return ConsumeEffect.clearAllStatusEffects();
			}

			case "teleport_randomly" -> {
				float diameter = optionalField("diameter", DataComponentParsers.FLOAT, value, 16.0f);
				return ConsumeEffect.teleportRandomlyEffect(diameter);
			}

			case "play_sound" -> {
				return ConsumeEffect.playSoundConsumeEffect(
						requiredField("sound", DataComponentParsers.NAMESPACED_KEY, value));
			}

			default -> throw new IllegalArgumentException("Unknown consume effect type " + type);
		}
	}
}
