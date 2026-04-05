package uk.co.notnull.CustomItems;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;

import uk.co.notnull.CustomItems.items.VanillaCustomItem;

public class Util {
	public static VanillaCustomItem getVanillaCustomItem(NamespacedKey key) {
		return new VanillaCustomItem(getVanillaItemType(key));
	}

	public static ItemType getVanillaItemType(NamespacedKey key) {
		if(!key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
			throw new IllegalArgumentException(key + " is not a Vanilla item");
		}

		return Registry.ITEM.getOrThrow(key);
	}

	public static boolean isVanillaItem(NamespacedKey key) {
		if(!key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
			return false;
		}

		return Registry.ITEM.get(key) != null;
	}
}
