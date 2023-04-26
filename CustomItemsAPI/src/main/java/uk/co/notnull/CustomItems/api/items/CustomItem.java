package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public interface CustomItem {
	static CustomItemBuilder.IDStep builder() {
		return CustomItemBuilder.builder();
	}

	String getId();

	Component getDisplayName();

	boolean isStamp();

	boolean isWearable();

	ItemStack createItem(CreationContext context);

	ItemStack createItem(CreationContext context, int amount);
}
