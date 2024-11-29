package uk.co.notnull.CustomItems.datacomponents.parsers;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ListParser<T, U> extends DataComponentTypeParser<List<T>, U> {
	public U parse(Object value) {
		if(value == null) {
			throw new NullPointerException("No value provided");
		}

		if(!(value instanceof List<?> list)) {
			throw new NullPointerException("Invalid value provided. Excepted List but got " + value.getClass().getName());
		}

		List<T> input = new ArrayList<>();

		for(int i = 0; i < list.size(); i++) {
			Object item = list.get(i);

			if(!getListItemType().isInstance(item)) {
				throw new IllegalArgumentException("List item #" + (i + 1) + ": expected " + getConfigType().getName() + " but got " + item.getClass().getName());
			}

			input.add(getListItemType().cast(item));
		}

		return doParse(input);
	}

	protected @NotNull Class<List<T>> getConfigType() {
		throw new NotImplementedException("Not implemented");
	}

	protected abstract @NotNull Class<T> getListItemType();
	protected abstract U doParse(List<T> value);
}
