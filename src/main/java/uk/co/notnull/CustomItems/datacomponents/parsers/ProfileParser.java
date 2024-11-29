package uk.co.notnull.CustomItems.datacomponents.parsers;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class ProfileParser extends DataComponentTypeParser<ConfigurationSection, ResolvableProfile> {
	private static final PropertyParser propertyParser = new PropertyParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected ResolvableProfile doParse(ConfigurationSection value) {
		String name = optionalField("name", DataComponentParsers.STRING, value);
		UUID uuid = optionalField("id", DataComponentParsers.UUID, value);
		List<ProfileProperty> properties = optionalListField("properties", propertyParser, value);

		ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();

		if(name != null) {
			builder.name(name);
		}

		if(uuid != null) {
			builder.uuid(uuid);
		}

		if(properties != null) {
			builder.addProperties(properties);
		}

		return builder.build();
	}

	private static class PropertyParser extends DataComponentTypeParser<ConfigurationSection, ProfileProperty> {
		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected ProfileProperty doParse(ConfigurationSection value) {
			return new ProfileProperty(requiredField("name", DataComponentParsers.STRING, value),
								requiredField("value", DataComponentParsers.STRING, value),
								optionalField("signature", DataComponentParsers.STRING, value));
		}
	}
}
