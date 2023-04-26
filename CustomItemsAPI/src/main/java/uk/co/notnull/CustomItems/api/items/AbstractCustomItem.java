package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractCustomItem implements CustomItem {
	protected final String id;
	protected final Component displayName;
	protected final boolean wearable;
	protected boolean stamp;

	public AbstractCustomItem(String id, Component displayName, boolean wearable, boolean stamp) {
		this.id = id;
		this.wearable = wearable;
		this.displayName = displayName;
		this.stamp = stamp;
	}

	public String getId() {
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