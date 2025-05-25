package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

@SuppressWarnings("UnstableApiUsage")
public class TagKeyParser<T extends Keyed> extends DataComponentTypeParser<Object, TagKey<T>> {
	private final RegistryKey<T> registryKey;

	public TagKeyParser(RegistryKey<T> registryKey) {
		this.registryKey = registryKey;
	}

	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected TagKey<T> doParse(Object value) {
		return TagKey.create(registryKey, DataComponentParsers.NAMESPACED_KEY.parse(value));
	}
}
