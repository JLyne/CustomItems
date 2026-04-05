package uk.co.notnull.CustomItems.items;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import uk.co.notnull.CustomItems.api.items.AbstractCustomItem;
import uk.co.notnull.CustomItems.api.items.CreationContext;

import java.util.Objects;

public final class VanillaCustomItem extends AbstractCustomItem {
	private final ItemType item;

	public VanillaCustomItem(ItemType item) {
		super(item.getKey(), Component.translatable(item.translationKey()), false);
		this.item = item;
	}

	public ItemType getItem() {
		return item;
	}

	@Override
	public String toString() {
		return "CustomItem{" +
				"id='" + id + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VanillaCustomItem that = (VanillaCustomItem) o;
		return Objects.equals(getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getItem(), getDisplayName());
	}

	@Override
	public ItemStack createItem(CreationContext context, int amount) {
		return this.item.createItemStack(Math.min(this.item.getMaxStackSize(), amount));
	}
}
