package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class OminousBottleAmplifierParser extends DataComponentTypeParser<Object, OminousBottleAmplifier> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected OminousBottleAmplifier doParse(Object value) {
		return OminousBottleAmplifier.amplifier(DataComponentParsers.INT.parse(value));
	}
}
