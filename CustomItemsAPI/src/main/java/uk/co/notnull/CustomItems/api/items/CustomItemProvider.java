package uk.co.notnull.CustomItems.api.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CustomItemProvider {
	public abstract List<CustomItem> provideItems();
	public abstract @Nullable CustomItem identifyItem(ItemStack item);
}
