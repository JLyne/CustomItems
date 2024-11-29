package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

public class DoubleParser extends DataComponentTypeParser<Object, Double> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected Double doParse(Object value) {
		if(value instanceof Number number) {
			return number.doubleValue();
		} else {
			try {
				return Double.parseDouble(value.toString());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Could not parse " + value + " as an double");
			}
		}
	}
}
