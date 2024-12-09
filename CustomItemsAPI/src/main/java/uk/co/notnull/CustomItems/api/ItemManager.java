package uk.co.notnull.CustomItems.api;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.api.items.provider.CustomItemProvider;

import java.util.Set;

@SuppressWarnings("unused")
public interface ItemManager {
	@Deprecated(forRemoval = true)
	default void registerProvider(uk.co.notnull.CustomItems.api.items.CustomItemProvider provider) {
		registerProvider((CustomItemProvider) provider);
	}

	void registerProvider(CustomItemProvider provider);

	@Deprecated(forRemoval = true)
	default void unregisterProvider(uk.co.notnull.CustomItems.api.items.CustomItemProvider provider) {
		unregisterProvider((CustomItemProvider) provider);
	}

	void unregisterProvider(CustomItemProvider provider);

	boolean isRegistered(CustomItem item);

	CustomItem getItem(NamespacedKey id);

	CustomItem getItem(ItemStack item);

	ItemStack createItem(NamespacedKey id, CreationContext context, int amount);

	void giveItem(Player player, NamespacedKey id, int amount);

	void giveItem(Player player, CustomItem item, int amount);

  	void grantItem(OfflinePlayer player, NamespacedKey id, int amount);

  	void grantItem(OfflinePlayer player, CustomItem item, int amount);

	int revokeItem(OfflinePlayer player, NamespacedKey id);

	int revokeItem(OfflinePlayer player, CustomItem item);

	@Deprecated(forRemoval = true)
    Set<NamespacedKey> getItemIds();

	boolean isValidId(NamespacedKey id);

	@Deprecated(forRemoval = true)
	default boolean isWearable(ItemStack item) {
		return false;
	}
}
