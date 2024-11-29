package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.UseCooldown;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class UseCooldownParser extends DataComponentTypeParser<ConfigurationSection, UseCooldown> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected UseCooldown doParse(ConfigurationSection value) {
		Integer seconds = requiredField("seconds", DataComponentParsers.INT, value);
		NamespacedKey cooldownGroup = optionalField("cooldown_group", DataComponentParsers.NAMESPACED_KEY, value);

		UseCooldown.Builder builder = UseCooldown.useCooldown(seconds);

		if(cooldownGroup != null) {
			builder.cooldownGroup(cooldownGroup);
		}

		return builder.build();
	}
}
