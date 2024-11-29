package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class RegistryKeySetParser<T extends Keyed> extends DataComponentTypeParser<Object, RegistryKeySet<T>> {
	private final RegistryKey<T> registryKey;
	private final Registry<T> registry;
	private final boolean allowTags;

	public RegistryKeySetParser(RegistryKey<T> registryKey, boolean allowTags) {
		this.allowTags = allowTags;
		this.registryKey = registryKey;
		this.registry = RegistryAccess.registryAccess().getRegistry(registryKey);
	}

	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected RegistryKeySet<T> doParse(Object value) {
		switch (value) {
			case null -> {
				return RegistrySet.keySet(registryKey, Collections.emptyList());
			}
			case List list -> {
				List<T> items = new ArrayList<>();

				for (String block : (List<String>) list) {
					items.add(registry.getOrThrow(DataComponentParsers.NAMESPACED_KEY.parse(block)));
				}

				return RegistrySet.keySetFromValues(registryKey, items);
			}
			case String string -> {
				if (string.startsWith("#")) {
					if (!allowTags) {
						throw new IllegalArgumentException("Tags not allowed");
					}

					TagKey<T> tag = TagKey.create(
							registryKey, DataComponentParsers.NAMESPACED_KEY.parse(string.replace("#", "")));

					return RegistrySet.keySet(registryKey, registry.getTag(tag).values());
				} else {
					return RegistrySet.keySetFromValues(registryKey, Collections.singletonList(
							registry.getOrThrow(DataComponentParsers.NAMESPACED_KEY.parse(string))));
				}
			}
			default -> throw new IllegalArgumentException("Key set must be list of strings or single string");
		}
	}
}
