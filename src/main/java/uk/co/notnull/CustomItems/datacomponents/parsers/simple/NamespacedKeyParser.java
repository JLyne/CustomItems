package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;


public class NamespacedKeyParser extends DataComponentTypeParser<String, NamespacedKey> {
	@Override
	protected @NotNull Class<String> getConfigType() {
		return String.class;
	}

	protected NamespacedKey doParse(String value) {
		return NamespacedKey.fromString(value);
	}
}
