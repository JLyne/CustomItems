package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.JukeboxSong;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

@SuppressWarnings("UnstableApiUsage")
public class JukeboxPlayableParser extends DataComponentTypeParser<String, JukeboxPlayable> {
	private static final RegistryLookupParser<JukeboxSong> registryParser =
			new RegistryLookupParser<>(RegistryKey.JUKEBOX_SONG);

	@Override
	protected @NotNull Class<String> getConfigType() {
		return String.class;
	}

	protected JukeboxPlayable doParse(String value) {
		JukeboxSong song = registryParser.parse(value);
		JukeboxPlayable.Builder builder = JukeboxPlayable.jukeboxPlayable(song);

		return builder.build();
	}
}
