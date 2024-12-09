package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public abstract class AbstractCustomItem implements CustomItem {
	protected final NamespacedKey id;
	protected final Component displayName;
	protected final boolean stamp;

	public AbstractCustomItem(NamespacedKey id, Component displayName, boolean stamp) {
		Objects.requireNonNull(id, "id cannot be null");
		Objects.requireNonNull(displayName, "name cannot be null");
		this.id = id;
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

	public ItemStack createItem(CreationContext context) {
		return createItem(context, 1);
	}

	public abstract ItemStack createItem(CreationContext context, int amount);

	@Override
	public String toString() {
		return "AbstractCustomItem{" +
				"id='" + id + '\'' +
				", stamp=" + stamp +
				'}';
	}
}