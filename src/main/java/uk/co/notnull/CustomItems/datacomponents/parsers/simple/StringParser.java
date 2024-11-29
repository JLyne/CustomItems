package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class StringParser extends DataComponentTypeParser<Object, String> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected String doParse(Object value) {
		return value.toString();
	}
}
