package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.ItemLore;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class LoreParser extends ListParser<String, ItemLore> {
	protected ItemLore doParse(List<String> value) {
		return ItemLore.lore()
				.lines(value.stream().map(DataComponentParsers.COMPONENT::parse).toList())
				.build();
	}

	@Override
	protected @NotNull Class<String> getListItemType() {
		return String.class;
	}
}
