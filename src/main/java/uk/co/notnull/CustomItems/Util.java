package uk.co.notnull.CustomItems;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import uk.co.notnull.CustomItems.items.VanillaCustomItem;

public class Util {
	public static VanillaCustomItem getVanillaCustomItem(NamespacedKey key) {
		return new VanillaCustomItem(getVanillaItemMaterial(key));
	}

	public static Material getVanillaItemMaterial(NamespacedKey key) {
		if(!key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
			throw new IllegalArgumentException(key + " is not a Vanilla item");
		}

		Material material = Material.matchMaterial(key.getKey());

		if(material == null || !material.isItem()) {
			throw new IllegalArgumentException(key + " is not a Vanilla item");
		}

		return material;
	}

	public static boolean isVanillaItem(NamespacedKey key) {
		if(!key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
			return false;
		}

		Material material = Material.matchMaterial(key.getKey());
		return material != null && material.isItem();
	}
}
