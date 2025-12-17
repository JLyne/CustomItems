package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.KineticWeapon;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class KineticWeaponParser extends DataComponentTypeParser<ConfigurationSection, KineticWeapon> {
	private static final ConditionParser conditionParser = new ConditionParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected KineticWeapon doParse(ConfigurationSection value) {
		Integer delayTicks = optionalField("delay_ticks", DataComponentParsers.INT, value);

		KineticWeapon.Condition damageConditions = optionalField("damage_conditions", conditionParser, value);
		KineticWeapon.Condition dismountConditions = optionalField("dismount_conditions", conditionParser, value);
		KineticWeapon.Condition knockbackConditions = optionalField("knockback_conditions", conditionParser, value);

		Float forwardMovement = optionalField("forward_movement", DataComponentParsers.FLOAT, value);
		Float damageMultiplier = optionalField("damage_multiplier", DataComponentParsers.FLOAT, value);

		NamespacedKey sound = optionalField("sound", DataComponentParsers.NAMESPACED_KEY, value);
		NamespacedKey hitSound = optionalField("hit_sound", DataComponentParsers.NAMESPACED_KEY, value);

		KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

		if(delayTicks != null) {
			builder.delayTicks(delayTicks);
		}

		if(damageConditions != null) {
			builder.damageConditions(damageConditions);
		}

		if(dismountConditions != null) {
			builder.dismountConditions(dismountConditions);
		}

		if(knockbackConditions != null) {
			builder.knockbackConditions(knockbackConditions);
		}

		if(forwardMovement != null) {
			builder.forwardMovement(forwardMovement);
		}

		if(damageMultiplier != null) {
			builder.damageMultiplier(damageMultiplier);
		}

		if(sound != null) {
			builder.sound(sound);
		}

		if(hitSound != null) {
			builder.hitSound(hitSound);
		}

		return builder.build();
	}

	private static class ConditionParser extends DataComponentTypeParser<ConfigurationSection, KineticWeapon.Condition> {
		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected KineticWeapon.Condition doParse(ConfigurationSection value) {
			Integer maxDurationTicks = requiredField("max_duration_ticks", DataComponentParsers.INT, value);
			float minSpeed = optionalField("min_speed", DataComponentParsers.FLOAT, value, 0.0f);
			float minRelativeSpeed = optionalField("min_relative_speed", DataComponentParsers.FLOAT, value, 0.0f);

			return KineticWeapon.condition(maxDurationTicks, minSpeed, minRelativeSpeed);
		}
	}
}
