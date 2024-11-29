package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Enchantable;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class EnchantableParser extends DataComponentTypeParser<Object, Enchantable> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected Enchantable doParse(Object value) {
		return Enchantable.enchantable(DataComponentParsers.INT.parse(value));
	}
}
