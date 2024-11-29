package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.MapId;
import java.lang.Object;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class MapIdParser extends DataComponentTypeParser<Object, MapId> {
	@Override
	protected @NotNull Class<Object> getConfigType() {
		return Object.class;
	}

	protected MapId doParse(Object value) {
		return MapId.mapId(DataComponentParsers.INT.parse(value));
	}
}
