package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class RegistryLookupParser<T extends Keyed> extends DataComponentTypeParser<String, T> {
	private final Registry<@NotNull T> registry;

	public RegistryLookupParser(RegistryKey<T> registryKey) {
		this.registry = RegistryAccess.registryAccess().getRegistry(registryKey);
	}

	@Override
	protected @NotNull Class<String> getConfigType() {
		return String.class;
	}

	protected T doParse(String value) {
		return registry.getOrThrow(DataComponentParsers.NAMESPACED_KEY.parse(value));
	}
}
