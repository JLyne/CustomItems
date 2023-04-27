package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractCustomItem implements CustomItem {
	protected final NamespacedKey id;
	protected final Component displayName;
	protected final boolean wearable;
	protected final boolean stamp;

	public AbstractCustomItem(NamespacedKey id, Component displayName, boolean wearable, boolean stamp) {
		this.id = id;
		this.wearable = wearable;
		this.displayName = displayName;
		this.stamp = stamp;
	}

	public NamespacedKey getId() {
		return id;
	}

	public Component getDisplayName() {
		return displayName;
	}

	public boolean isStamp() {
		return stamp;
	}

	public boolean isWearable() {
		return wearable;
	}

	public ItemStack createItem(CreationContext context) {
		return createItem(context, 1);
	}

	public abstract ItemStack createItem(CreationContext context, int amount);

	@Override
	public String toString() {
		return "AbstractCustomItem{" +
				"id='" + id + '\'' +
				", wearable=" + wearable +
				", stamp=" + stamp +
				'}';
	}
}