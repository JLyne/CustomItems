package uk.co.notnull.CustomItems.api.loot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public interface LootManager {
	@Nullable ItemStack generateLoot(String pool, Player player, int amount);
	boolean isValidCategory(String category);
}
