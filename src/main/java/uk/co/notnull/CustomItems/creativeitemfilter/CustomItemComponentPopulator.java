package uk.co.notnull.CustomItems.creativeitemfilter;

import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.inventory.ItemStack;
import org.hurricanegames.creativeitemfilter.CreativeItemFilterConfiguration;
import org.hurricanegames.creativeitemfilter.handler.component.ItemComponentPopulator;
import org.hurricanegames.creativeitemfilter.utils.ItemComponentUtils;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.ItemDataManager;
import uk.co.notnull.CustomItems.api.ItemManager;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.items.ConfigCustomItem;

public class CustomItemComponentPopulator implements ItemComponentPopulator {
	private final ItemManager itemManager;

	public CustomItemComponentPopulator(CustomItemsImpl plugin) {
		this.itemManager = plugin.getItemManager();
	}

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void populateComponents(@NotNull ItemStack oldItem, @NotNull ItemStack newItem,
								   CreativeItemFilterConfiguration creativeItemFilterConfiguration) {
		CustomItem item = itemManager.getItem(oldItem);

		if(!(item instanceof ConfigCustomItem configItem)) {
			return;
		}

		ItemDataManager.copyItemData(oldItem, newItem);

		for(DataComponentType type: configItem.getComponentTypes()) {
			if(type instanceof DataComponentType.Valued<?> valued) {
				ItemComponentUtils.copyComponent(oldItem, newItem, valued);
			} else if(type instanceof DataComponentType.NonValued nonValued) {
				newItem.setData(nonValued);
			}
		}
	}
}
