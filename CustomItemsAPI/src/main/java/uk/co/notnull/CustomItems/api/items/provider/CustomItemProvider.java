package uk.co.notnull.CustomItems.api.items.provider;


import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import uk.co.notnull.CustomItems.api.items.CustomItem;

import java.util.Collection;

public interface CustomItemProvider {
	Collection<CustomItem> provideItems();
	@Nullable CustomItem identifyItem(ItemStack item);
}
