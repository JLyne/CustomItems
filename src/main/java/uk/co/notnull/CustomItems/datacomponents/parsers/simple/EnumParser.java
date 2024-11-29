package uk.co.notnull.CustomItems.datacomponents.parsers.simple;

import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.logging.Level;

public class EnumParser<T extends Enum> extends DataComponentTypeParser<String, T> {
	private final Class<T> clazz;
	private final Method method;

	public EnumParser(Class<T> clazz) {
		this.clazz = clazz;

		try {
			this.method = clazz.getMethod("valueOf", String.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected @NotNull Class<String> getConfigType() {
		return String.class;
	}

	protected T doParse(String value) {
		try {
			return clazz.cast(method.invoke(null, value.toUpperCase(Locale.ROOT)));
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to invoke valueOf for enum" + clazz.getName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		}
	}
}
