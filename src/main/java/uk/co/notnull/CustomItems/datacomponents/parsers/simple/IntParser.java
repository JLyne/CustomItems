package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class IntParser extends DataComponentTypeParser<Object, Integer> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected Integer doParse(Object value) {
		if(value instanceof Number number) {
			return number.intValue();
		} else {
			try {
				return Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Could not parse " + value + " as an integer");
			}
		}
	}
}
