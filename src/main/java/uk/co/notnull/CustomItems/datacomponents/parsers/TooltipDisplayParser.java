package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

import java.util.HashSet;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TooltipDisplayParser extends DataComponentTypeParser<ConfigurationSection, TooltipDisplay> {
	private static final RegistryLookupParser<DataComponentType> patternParser =
				new RegistryLookupParser<>(RegistryKey.DATA_COMPONENT_TYPE);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected TooltipDisplay doParse(ConfigurationSection value) {
		Boolean hideTooltip = optionalField("hide_tooltip", DataComponentParsers.BOOLEAN, value);
		List<DataComponentType> hiddenComponents = optionalListField("rules", patternParser, value);

		TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();

		if(hideTooltip != null) {
			builder.hideTooltip(hideTooltip);
		}

		if(hiddenComponents != null) {
			builder.hiddenComponents(new HashSet<>(hiddenComponents));
		}

		return builder.build();
	}
}
