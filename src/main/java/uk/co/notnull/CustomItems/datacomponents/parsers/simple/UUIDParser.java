package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

import java.util.UUID;

public class UUIDParser extends DataComponentTypeParser<Object, UUID> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected UUID doParse(Object value) {
		return UUID.fromString(value.toString());
	}
}
