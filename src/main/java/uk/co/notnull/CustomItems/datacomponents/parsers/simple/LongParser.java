package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class LongParser extends DataComponentTypeParser<Object, Long> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected Long doParse(Object value) {
		if(value instanceof Number number) {
			return number.longValue();
		} else {
			try {
				return Long.parseLong(value.toString());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Could not parse " + value + " as a long");
			}
		}
	}
}
