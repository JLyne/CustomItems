package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.PiercingWeapon;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class PiercingWeaponParser extends DataComponentTypeParser<ConfigurationSection, PiercingWeapon> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected PiercingWeapon doParse(ConfigurationSection value) {
		Boolean dealsKnockback = optionalField("deals_knockback", DataComponentParsers.BOOLEAN, value);
		Boolean dismounts = optionalField("dismounts", DataComponentParsers.BOOLEAN, value);

		NamespacedKey sound = optionalField("sound", DataComponentParsers.NAMESPACED_KEY, value);
		NamespacedKey hitSound = optionalField("hit_sound", DataComponentParsers.NAMESPACED_KEY, value);

		PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

		if(dealsKnockback != null) {
			builder.dealsKnockback(dealsKnockback);
		}

		if(dismounts != null) {
			builder.dismounts(dismounts);
		}

		if(sound != null) {
			builder.sound(sound);
		}

		if(hitSound != null) {
			builder.hitSound(hitSound);
		}

		return builder.build();
	}
}
