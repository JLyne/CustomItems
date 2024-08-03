package uk.co.notnull.CustomItems.loot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.items.CreationContextImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LootPool extends ArrayList<CustomItem> {
	String name;
	private static final Random random = new Random();

	public LootPool(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ItemStack generateItem(Player player, int amount) {
		CustomItem item = get(random.nextInt(size()));

		ItemStack itemStack = item.createItem(new CreationContextImpl(player, CreationReason.LOOT), amount);
		itemStack.setAmount(Math.min(amount, itemStack.getMaxStackSize()));

		return itemStack;
	}

	public void giveContents(Player player) {
		List<ItemStack> created = new ArrayList<>();
		CreationContextImpl context = new CreationContextImpl(player, CreationReason.GIVEN);

		forEach((CustomItem item) -> created.add(item.createItem(context)));

		Inventory inventory = player.getInventory();

        final Map<Integer, ItemStack> map = inventory.addItem(created.toArray(new ItemStack[0]));

        map.values().forEach((ItemStack item) -> player.getWorld().dropItemNaturally(player.getLocation(), item));
	}
}
