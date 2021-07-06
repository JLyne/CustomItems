package uk.co.notnull.CustomItems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CustomItem {
	private final String id;
	private final Material item;
	private final int model;
	private final String name;
	private final List<String> lore;

	private final boolean wearable;
	private boolean loot = false;
	private LootTier lootTier = null;
	private boolean stamp = true;
	private String lootCategory = null;

	private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

	public CustomItem(String id, Material item, int model, String name, List<String> lore, boolean wearable) {
		this.id = id;
		this.item = item;
		this.model = model;
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.lore = lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
		this.wearable = wearable;
	}

	public CustomItem(String id, Material item, int model, String name, List<String> lore, boolean wearable,
					  String lootCategory, LootTier lootTier, boolean stamp) {
		this(id, item, model, name, lore, wearable);

		this.lootCategory = lootCategory;
		this.loot = true;
		this.lootTier = lootTier;
		this.stamp = stamp;
	}

	public String getId() {
		return id;
	}

	public Material getItem() {
		return item;
	}

	public int getModel() {
		return model;
	}

	public String getName() {
		return name;
	}

	public String getName(OfflinePlayer player) {
		if(player != null && player.getName() != null) {
			return name.replace("{player}", player.getName());
		}

		return name;
	}

	public Component getDisplayName(OfflinePlayer player) {
		if(player != null && player.getName() != null) {
			return legacySerializer.deserialize(name.replace("{player}", player.getName()));
		}

		return legacySerializer.deserialize(name);
	}

	public List<Component> getLore() {
		return lore.stream().map(legacySerializer::deserialize).collect(Collectors.toList());
	}

	public List<Component> getLore(OfflinePlayer player) {
		if(player != null && player.getName() != null) {
			return lore.stream().map(line -> {
				line = line.replace("{player}", player.getName());
				return legacySerializer.deserialize(line);
			}).collect(Collectors.toList());
		} else {
			return getLore();
		}
	}

	public boolean isLoot() {
		return loot;
	}

	public LootTier getLootTier() {
		return lootTier;
	}

	public boolean isStamp() {
		return stamp;
	}

	public String getLootCategory() {
		return lootCategory;
	}

	public boolean isWearable() {
		return wearable;
	}

	@Override
	public String toString() {
		return "CustomItem{" +
				"id='" + id + '\'' +
				", item=" + item +
				", model=" + model +
				", name='" + name + '\'' +
				", wearable=" + wearable +
				", loot=" + loot +
				", lootTier=" + lootTier +
				", stamp=" + stamp +
				", lootCategory='" + lootCategory + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CustomItem that = (CustomItem) o;
		return getModel() == that.getModel() && isWearable() == that.isWearable() && isLoot() == that.isLoot() && isStamp() == that.isStamp() && Objects.equals(
				getId(), that.getId()) && getItem() == that.getItem() && Objects.equals(getName(),
																						that.getName()) && Objects.equals(
				getLore(), that.getLore()) && getLootTier() == that.getLootTier() && Objects.equals(
				getLootCategory(), that.getLootCategory());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getItem(), getModel(), getName(), getLore(), isWearable(), isLoot(), getLootTier(),
							isStamp(), getLootCategory());
	}
}

