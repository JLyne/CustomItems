package uk.co.notnull.CustomItems.loot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.items.CreationContextImpl;

import java.util.ArrayList;
import java.util.Random;

public class LootPool extends ArrayList<CustomItem> {
	String name;
	private static final Random random = new Random();

	public ItemStack generateItem(Player player, int amount) {
		CustomItem item = get(random.nextInt(size()));

		return item.createItem(new CreationContextImpl(player, CreationReason.LOOT), amount);
	}
}
