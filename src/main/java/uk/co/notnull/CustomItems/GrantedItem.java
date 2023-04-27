package uk.co.notnull.CustomItems;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SerializableAs("GrantedItem")
public class GrantedItem implements ConfigurationSerializable {
	private final NamespacedKey item;
	private final int amount;
	private final UUID player;

	public GrantedItem(NamespacedKey item, int amount, UUID player) {
		this.item = item;
		this.amount = amount;
		this.player = player;
	}

	public GrantedItem(Map<String, Object> data) {
		this.item = NamespacedKey.fromString(data.get("item").toString(), CustomItemsImpl.getInstance());
		this.player = UUID.fromString(data.get("player").toString());
		this.amount = Integer.parseInt(data.get("amount").toString());
	}

	public NamespacedKey getItem() {
		return item;
	}

	public int getAmount() {
		return amount;
	}

	public UUID getPlayer() {
		return player;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GrantedItem that = (GrantedItem) o;
		return getAmount() == that.getAmount() &&
				getItem().equals(that.getItem()) &&
				getPlayer().equals(that.getPlayer());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getItem(), getAmount(), getPlayer());
	}

	@Override
	public String toString() {
		return "GrantedItem{" +
				"item='" + item + '\'' +
				", amount=" + amount +
				", player=" + player +
				'}';
	}

	@NotNull
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("item", item);
		map.put("amount", amount);
		map.put("player", player.toString());
		return map;
	}
}

