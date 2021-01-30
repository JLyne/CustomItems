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
	private final int damage;
	private final int model;
	private final String name;
	private final List<String> lore;

	public CustomItem(String id, Material item, int damage, int model, String name, List<String> lore) {
		this.id = id;
		this.item = item;
		this.damage = damage;
		this.model = model;
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.lore = lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
	}

	public String getId() {
		return id;
	}

	public Material getItem() {
		return item;
	}

	public int getDamage() {
		return damage;
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

	@Override
	public String toString() {
		return "CustomItem{" +
				"id='" + id + '\'' +
				", item=" + item +
				", damage=" + damage +
				", model=" + model +
				", name='" + name + '\'' +
				", lore=" + lore +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CustomItem that = (CustomItem) o;
		return Float.compare(that.getDamage(), getDamage()) == 0 &&
				Float.compare(that.getModel(), getModel()) == 0 &&
				getId().equals(that.getId()) &&
				getItem().equals(that.getItem()) &&
				getName().equals(that.getName()) &&
				getLore().equals(that.getLore());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getItem(), getDamage(), getModel(), getName(), getLore());
	}
}

