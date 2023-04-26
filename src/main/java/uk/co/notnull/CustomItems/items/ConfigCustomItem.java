package uk.co.notnull.CustomItems.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.co.notnull.CustomItems.ItemDataManager;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.AbstractCustomItem;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ConfigCustomItem extends AbstractCustomItem {
	private final Material item;
	private final int model;
	private final List<String> lore;
	private static final MiniMessage miniMessage = MiniMessage.builder().build();

	public ConfigCustomItem(String id, Material item, int model, Component name, List<String> lore, boolean wearable, boolean stamp) {
		super(id, name, wearable, stamp);
		this.item = item;
		this.model = model;
		this.lore = lore;
	}

	public Material getItem() {
		return item;
	}

	public int getModel() {
		return model;
	}

	public List<Component> getLore() {
		return lore.stream().map(miniMessage::deserialize).collect(Collectors.toList());
	}

	public List<Component> getLore(OfflinePlayer player) {
		if(player != null && player.getName() != null) {
			return lore.stream().map(line -> {
				line = line.replace("<player>", player.getName());
				return miniMessage.deserialize(line);
			}).collect(Collectors.toList());
		} else {
			return getLore();
		}
	}

	@Override
	public String toString() {
		return "CustomItem{" +
				"id='" + id + '\'' +
				", displayName='" + displayName + '\'' +
				", item=" + item +
				", model=" + model +
				", wearable=" + wearable +
				", stamp=" + stamp +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigCustomItem that = (ConfigCustomItem) o;
		return getModel() == that.getModel() && isWearable() == that.isWearable() && isStamp() == that.isStamp() && Objects.equals(
				getId(), that.getId()) && getItem() == that.getItem() &&
				Objects.equals(getDisplayName(), that.getDisplayName()) && Objects.equals(
				getLore(), that.getLore());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getItem(), getModel(), getDisplayName(), getLore(), isWearable(), isStamp());
	}

	@Override
	public ItemStack createItem(CreationContext context, int amount) {
		ItemStack item = new ItemStack(getItem(), amount);
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);

		meta.setCustomModelData(getModel());

		ItemDataManager.populateItemData(meta.getPersistentDataContainer(), this, context.player());

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		meta.displayName(getDisplayName());
		meta.lore(getLore(context.player()));
		item.setItemMeta(meta);

		return item;
	}
}

