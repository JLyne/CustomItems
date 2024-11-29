package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.MapDecorations;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapCursor;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

@SuppressWarnings("UnstableApiUsage")
public class MapDecorationsParser extends DataComponentTypeParser<ConfigurationSection, MapDecorations> {
	private static final DecorationEntryParser entryParser = new DecorationEntryParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected MapDecorations doParse(ConfigurationSection value) {
		MapDecorations.Builder builder = MapDecorations.mapDecorations();

		value.getValues(false).forEach((k, v) -> builder.put(k, entryParser.parse(v)));

		return builder.build();
	}

	private static class DecorationEntryParser extends DataComponentTypeParser<ConfigurationSection, MapDecorations.DecorationEntry> {
		private final RegistryLookupParser<MapCursor.Type> registryParser =
				new RegistryLookupParser<>(RegistryKey.MAP_DECORATION_TYPE);

		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		protected MapDecorations.DecorationEntry doParse(ConfigurationSection value) {
			MapCursor.Type type = requiredField("type", registryParser, value);
			double x = requiredField("x", DataComponentParsers.DOUBLE, value);
			double z = requiredField("z", DataComponentParsers.DOUBLE, value);
			float rotation = requiredField("rotation", DataComponentParsers.FLOAT, value);

			return MapDecorations.decorationEntry(type, x, z, rotation);
		}
	}
}
