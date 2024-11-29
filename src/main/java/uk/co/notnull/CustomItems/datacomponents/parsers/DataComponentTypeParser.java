package uk.co.notnull.CustomItems.datacomponents.parsers;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class DataComponentTypeParser<T, U> {
	public U parse(Object value) {
		if(value == null) {
			throw new NullPointerException("No value provided");
		}

		if(!getConfigType().isInstance(value)) {
			throw new NullPointerException("Expected " + getConfigType().getName() + ", but got " + value.getClass().getName());
		}

		return doParse(getConfigType().cast(value));
	}

	public List<U> parseList(Object value) {
		if(value == null) {
			throw new NullPointerException("No value provided");
		}

		if(!(value instanceof List<?> list)) {
			throw new NullPointerException("Excepted List but got " + value.getClass().getName());
		}

		List<U> result = new ArrayList<>();

		for(int i = 0; i < list.size(); i++) {
			Object item = list.get(i);

			if(!getConfigType().isInstance(item)) {
				throw new IllegalArgumentException("List item #" + (i + 1) + ": Expected " + getConfigType().getName() + " but got " + item.getClass().getName());
			}

			result.add(doParse(getConfigType().cast(item)));
		}

		return result;
	}

	protected abstract @NotNull Class<T> getConfigType();
	protected abstract U doParse(T value);

	protected static <C> @NotNull C requiredField(String key, DataComponentTypeParser<?, C> parser,
												  ConfigurationSection config) {
		if(!config.contains(key) || config.get(key) == null) {
			throw new IllegalArgumentException("Missing required field " + key);
		}

		try {
			return parser.parse(config.get(key));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Field " + key + ": " + ex.getMessage(), ex);
		}
	}

	protected static <C> @Nullable C optionalField(String key, DataComponentTypeParser<?, C> parser,
												   ConfigurationSection config) {
		if(!config.contains(key) || config.get(key) == null) {
			return null;
		}

		try {
			return parser.parse(config.get(key));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Field " + key + ": " + ex.getMessage(), ex);
		}
	}

	protected static <C> @NotNull C optionalField(String key, DataComponentTypeParser<?, C> parser,
												   ConfigurationSection config, @NotNull C defaultValue) {
		if(!config.contains(key) || config.get(key) == null) {
			return defaultValue;
		}

		try {
			C value = parser.parse(config.get(key));

			return value == null ? defaultValue : value;
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Field " + key + ": " + ex.getMessage(), ex);
		}
	}

	protected static <C> @NotNull List<C> requiredListField(String key, DataComponentTypeParser<?, C> parser,
															 ConfigurationSection config) {
		if(!config.contains(key)) {
			throw new IllegalArgumentException("Missing required field " + key);
		}

		try {
			return parser.parseList(config.get(key));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Field " + key + ": " + ex.getMessage(), ex);
		}
	}

	protected static <C> @Nullable List<C> optionalListField(String key, DataComponentTypeParser<?, C> parser,
															 ConfigurationSection config) {
		if(!config.contains(key) || config.get(key) == null) {
			return null;
		}

		try {
			return parser.parseList(config.get(key));
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("Field " + key + ": " + ex.getMessage(), ex);
		}
	}
}
