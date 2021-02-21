package uk.co.notnull.CustomItems;

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

	private boolean loot = false;
	private LootTier lootTier = null;
	private boolean stamp = true;
	private String lootCategory = null;

	public CustomItem(String id, Material item, int model, String name, List<String> lore) {
		this.id = id;
		this.item = item;
		this.model = model;
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.lore = lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
	}

	public CustomItem(String id, Material item, int model, String name, List<String> lore, String lootCategory,
					  LootTier lootTier, boolean stamp) {
		this(id, item, model, name, lore);

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

	public List<String> getLore() {
		return lore;
	}

	public List<String> getLore(OfflinePlayer player) {
		if(player != null && player.getName() != null) {
			return lore.stream().map(line -> line.replace("{player}", player.getName())).collect(Collectors.toList());
		} else {
			return lore;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CustomItem that = (CustomItem) o;
		return getModel() == that.getModel() && isLoot() == that.isLoot() && isStamp() == that.isStamp() && getId().equals(
				that.getId()) && getItem() == that.getItem() && getName().equals(that.getName()) && getLore().equals(
				that.getLore()) && Objects.equals(getLootTier(), that.getLootTier()) && Objects.equals(
				getLootCategory(), that.getLootCategory());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getItem(), getModel(), getName(), getLore(), isLoot(), getLootTier(), isStamp(),
							getLootCategory());
	}
}

