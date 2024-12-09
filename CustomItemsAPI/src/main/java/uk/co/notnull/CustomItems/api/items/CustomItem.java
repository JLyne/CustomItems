package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public interface CustomItem {
	static CustomItemBuilder.IDStep builder() {
		return CustomItemBuilder.builder();
	}

	NamespacedKey getId();

	Component getDisplayName();

	boolean isStamp();

	@Deprecated(forRemoval = true)
	default boolean isWearable() {
		return false;
	}

	ItemStack createItem(CreationContext context);

	ItemStack createItem(CreationContext context, int amount);
}
