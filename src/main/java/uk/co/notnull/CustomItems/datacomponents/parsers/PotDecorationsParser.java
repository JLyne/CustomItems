package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.PotDecorations;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PotDecorationsParser extends ListParser<String, PotDecorations> {
	private static final RegistryLookupParser<ItemType> registryParser = new RegistryLookupParser<>(RegistryKey.ITEM);

	@Override
	protected @NotNull Class<String> getListItemType() {
		return String.class;
	}

	protected PotDecorations doParse(List<String> value) {
		ItemType back = null;
		ItemType left = null;
		ItemType right = null;
		ItemType front = null;

		if(!value.isEmpty()) {
			back = registryParser.parse(value.getFirst());
		}

		if(value.size() >= 2) {
			left = registryParser.parse(value.getFirst());
		}

		if(value.size() >= 3) {
			right = registryParser.parse(value.getFirst());
		}

		if(value.size() >= 4) {
			front = registryParser.parse(value.getFirst());
		}

		return PotDecorations.potDecorations(back, left, right, front);
	}
}
