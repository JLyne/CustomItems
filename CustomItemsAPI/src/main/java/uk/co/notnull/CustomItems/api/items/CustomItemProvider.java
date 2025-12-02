package uk.co.notnull.CustomItems.api.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@Deprecated(forRemoval = true)
public abstract class CustomItemProvider implements uk.co.notnull.CustomItems.api.items.provider.CustomItemProvider {
	public abstract @NotNull Collection<CustomItem> provideItems();
	public abstract @Nullable CustomItem identifyItem(ItemStack item);
}
