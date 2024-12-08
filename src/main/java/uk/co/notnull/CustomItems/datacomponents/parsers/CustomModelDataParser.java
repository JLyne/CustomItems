package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CustomModelDataParser extends DataComponentTypeParser<Object, CustomModelData> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected CustomModelData doParse(Object value) {
		CustomModelData.Builder customModelData = CustomModelData.customModelData();

		if(value instanceof ConfigurationSection section) {
			List<Float> floats = optionalListField("floats", DataComponentParsers.FLOAT, section);
			List<String> strings = optionalListField("strings", DataComponentParsers.STRING, section);
			List<Boolean> flags = optionalListField("flags", DataComponentParsers.BOOLEAN, section);
			List<Color> colors = optionalListField("colors", DataComponentParsers.COLOR, section);

			if(floats != null) {
				customModelData.addFloats(floats);
			}

			if(strings != null) {
				customModelData.addStrings(strings);
			}

			if(flags != null) {
				customModelData.addFlags(flags);
			}

			if(colors != null) {
				customModelData.addColors(colors);
			}
		} else {
			addValue(customModelData, value);
		}

		return customModelData.build();
	}

	private void addValue(CustomModelData.Builder customModelData, Object value) {
		if(value instanceof String string) {
			customModelData.addString(string);
		} else if(value instanceof Number number) {
			customModelData.addFloat(number.floatValue());
		} else if(value instanceof Boolean bool) {
			customModelData.addFlag(bool);
		} else if(value instanceof List list) {
			for(Object item: list) {
				addValue(customModelData, item);
			}
		}
	}
}
