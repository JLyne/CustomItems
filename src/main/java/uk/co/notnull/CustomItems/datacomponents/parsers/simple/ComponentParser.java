package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class ComponentParser extends DataComponentTypeParser<String, Component> {
	private static final MiniMessage miniMessage = MiniMessage.miniMessage();

	@Override
	protected @NotNull Class<String> getConfigType() {
		return String.class;
	}

	protected Component doParse(String value) {
		return miniMessage.deserialize(value);
	}
}
