package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class WeaponParser extends DataComponentTypeParser<ConfigurationSection, Weapon> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Weapon doParse(ConfigurationSection value) {
		Float disableBlockingForSeconds = optionalField("disable_blocking_for_seconds", DataComponentParsers.FLOAT, value);
		Integer itemDamagePerAttack = optionalField("item_damage_per_attack", DataComponentParsers.INT, value);

		Weapon.Builder builder = Weapon.weapon();

		if(disableBlockingForSeconds != null) {
			builder.disableBlockingForSeconds(disableBlockingForSeconds);
		}

		if(itemDamagePerAttack != null) {
			builder.itemDamagePerAttack(itemDamagePerAttack);
		}

		return builder.build();
	}
}
