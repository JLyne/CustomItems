package uk.co.notnull.CustomItems.items;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.api.items.AbstractCustomItem;
import uk.co.notnull.CustomItems.api.items.CreationContext;

import java.util.Objects;

public final class VanillaCustomItem extends AbstractCustomItem {
	private final Material item;

	public VanillaCustomItem(Material item) {
		super(item.getKey(), Component.translatable(item.translationKey()), false, false);
		this.item = item;
	}

	public Material getItem() {
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
		ItemStack item = new ItemStack(getItem(), amount);
		item.setAmount(Math.min(item.getMaxStackSize(), amount));

		return item;
	}
}
