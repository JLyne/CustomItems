package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class FloatParser extends DataComponentTypeParser<Object, Float> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected Float doParse(Object value) {
		if(value instanceof Number number) {
			return number.floatValue();
		} else {
			try {
				return Float.parseFloat(value.toString());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Could not parse " + value + " as a float");
			}
		}
	}
}
