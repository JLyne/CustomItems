package uk.co.notnull.CustomItems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.api.ItemManager;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.items.CreationContextImpl;

import java.util.ArrayList;
import java.util.List;

public final class ClaimGui implements InventoryHolder, Listener {
	private final Inventory inventory;
	private final ArrayList<ItemStack> items;
	private final List<GrantedItem> grantedItems;

	public ClaimGui(CustomItemsImpl plugin, Player player, List<GrantedItem> grantedItems) {
		int rows = grantedItems.size() / 9 + ((grantedItems.size() % 9 == 0) ? 0 : 1);
		int inventorySize = Math.max(1, Math.min(rows, 6)) * 9;

		ItemManager manager = plugin.getItemManager();
		this.grantedItems = grantedItems;

		items = new ArrayList<>();
		CreationContext context = new CreationContextImpl(player, CreationReason.GRANTED);

		for (GrantedItem unclaimedItem : grantedItems) {
			if(manager.isValidId(unclaimedItem.getItem())) {
				items.add(manager.createItem(unclaimedItem.getItem(), context, unclaimedItem.getAmount()));
			} else {
				ItemStack air = new ItemStack(Material.AIR, 1);
				items.add(air);
			}
		}

		inventory = Bukkit.getServer().createInventory(player, inventorySize);

		ItemStack[] contents = new ItemStack[items.size()];
		items.toArray(contents);
		inventory.setContents(contents);

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@NotNull
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public List<GrantedItem> getGrantedItems() {
		return grantedItems;
	}

	public List<ItemStack> getItems() {
		return items;
	}
}
