package uk.co.notnull.CustomItems.api;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.api.items.CustomItemProvider;

import java.util.Set;

@SuppressWarnings("unused")
public interface ItemManager {
	void registerProvider(CustomItemProvider provider);

	void unregisterProvider(CustomItemProvider provider);

	boolean isRegistered(CustomItem item);

	CustomItem getItem(NamespacedKey id);

	CustomItem getItem(ItemStack item);

	ItemStack createItem(NamespacedKey id, CreationContext context, int amount);

	void giveItem(Player player, NamespacedKey id, int amount);

	void giveItem(Player player, CustomItem item, int amount);

  	void grantItem(OfflinePlayer player, NamespacedKey id, int amount);

  	void grantItem(OfflinePlayer player, CustomItem item, int amount);

	void giveCategory(Player player, String category);

    Set<NamespacedKey> getItemIds();

	boolean isValidId(NamespacedKey id);

	boolean isWearable(ItemStack item);
}
