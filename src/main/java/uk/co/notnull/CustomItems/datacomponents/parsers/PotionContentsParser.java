package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.EnumParser;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PotionContentsParser extends DataComponentTypeParser<ConfigurationSection, PotionContents> {
	private static final EnumParser<PotionType> potionTypeParser = new EnumParser<>(PotionType.class);
	private static final RegistryLookupParser<PotionEffectType> registryParser =
			new RegistryLookupParser<>(RegistryKey.MOB_EFFECT);
	private static final PotionEffectParser potionEffectParser = new PotionEffectParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected PotionContents doParse(ConfigurationSection value) {
		PotionContents.Builder builder = PotionContents.potionContents();
		PotionType potion = optionalField("potion", potionTypeParser, value);
		Color customColor = optionalField("custom_color", DataComponentParsers.COLOR, value);
		String customName = optionalField("custom_name", DataComponentParsers.STRING, value);
		List<PotionEffect> customEffects = optionalListField("custom_effects", potionEffectParser, value);

		//TODO: Optional string (NamespacedKey) field potion

		if(value.contains("potion")) {
			builder.potion(potion);
		}

		if(customColor != null) {
			builder.customColor(customColor);
		}

		if(customName != null) {
			builder.customName(customName);
		}

		if(customEffects != null) {
			builder.addCustomEffects(customEffects);
		}

		return builder.build();
	}

	public static class PotionEffectParser extends DataComponentTypeParser<ConfigurationSection, PotionEffect> {
		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected PotionEffect doParse(ConfigurationSection value) {
			PotionEffectType type = requiredField("id", registryParser, value);
			int amplifier = requiredField("amplifier", DataComponentParsers.INT, value);
			int duration = requiredField("duration", DataComponentParsers.INT, value);
			boolean ambient = requiredField("ambient", DataComponentParsers.BOOLEAN, value);
			boolean particles = requiredField("show_particles", DataComponentParsers.BOOLEAN, value);
			boolean icon = requiredField("show_icon", DataComponentParsers.BOOLEAN, value);

			return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
		}
	}
}
