package uk.co.notnull.CustomItems.api.items.provider;


import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import uk.co.notnull.CustomItems.api.items.CustomItem;

import java.util.List;

public interface CustomItemProvider {
	List<CustomItem> provideItems();
	@Nullable CustomItem identifyItem(ItemStack item);
}
