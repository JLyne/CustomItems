package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.CustomModelData;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class CustomModelDataParser extends DataComponentTypeParser<Object, CustomModelData> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected CustomModelData doParse(Object value) {
		return CustomModelData.customModelData(DataComponentParsers.INT.parse(value));
	}
}
