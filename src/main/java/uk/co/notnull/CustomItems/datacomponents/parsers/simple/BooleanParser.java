package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class BooleanParser extends DataComponentTypeParser<Boolean, Boolean> {
	@Override
	protected @NotNull Class<Boolean> getConfigType() {
		return Boolean.class;
	}

	protected Boolean doParse(Boolean value) {
		return value;
	}
}
