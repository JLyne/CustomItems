package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.JukeboxSong;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

@SuppressWarnings("UnstableApiUsage")
public class JukeboxPlayableParser extends DataComponentTypeParser<ConfigurationSection, JukeboxPlayable> {
	private static final RegistryLookupParser<JukeboxSong> registryParser =
			new RegistryLookupParser<>(RegistryKey.JUKEBOX_SONG);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected JukeboxPlayable doParse(ConfigurationSection value) {
		JukeboxSong song = requiredField("song", registryParser, value);
		JukeboxPlayable.Builder builder = JukeboxPlayable.jukeboxPlayable(song);

		builder.showInTooltip(DataComponentParsers.SHOW_IN_TOOLTIP.parse(value));

		return builder.build();
	}
}
