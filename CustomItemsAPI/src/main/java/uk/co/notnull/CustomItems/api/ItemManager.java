package uk.co.notnull.CustomItems.api;

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

	CustomItem getItem(String id);

	CustomItem getItem(ItemStack item);

	ItemStack createItem(String id, CreationContext context, int amount);

	void giveItem(Player player, String id, int amount);

	void giveItem(Player player, CustomItem item, int amount);

  	void grantItem(OfflinePlayer player, String id, int amount);

  	void grantItem(OfflinePlayer player, CustomItem item, int amount);

	void giveCategory(Player player, String category);

    Set<String> getItemIds();

	boolean isValidId(String id);

	boolean isWearable(ItemStack item);
}
