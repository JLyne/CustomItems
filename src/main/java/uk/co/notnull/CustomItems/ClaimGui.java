package uk.co.notnull.CustomItems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ClaimGui implements InventoryHolder, Listener {
	private final CustomItems plugin;
	private Inventory inventory;
	private Player player;

	private ArrayList<ItemStack> items;
	private List<GrantedItem> grantedItems;

	public ClaimGui(CustomItems plugin, Player player, List<GrantedItem> grantedItems) {

		int rows = grantedItems.size() / 9 + ((grantedItems.size() % 9 == 0) ? 0 : 1);
		int inventorySize = Math.max(1, Math.min(rows, 6)) * 9;

		Bukkit.getLogger().info("" + grantedItems.size());
		Bukkit.getLogger().info("" + grantedItems.size() / 9);
		Bukkit.getLogger().info("" + Math.min(grantedItems.size() / 9, 6));
		Bukkit.getLogger().info("" + Math.max(1, Math.min(grantedItems.size() / 9, 6)));
		Bukkit.getLogger().info("" + inventorySize);

		this.plugin = plugin;
		this.grantedItems = grantedItems;
		this.player = player;

		items = new ArrayList<>();

		for (GrantedItem unclaimedItem : grantedItems) {
			items.add(plugin.getItemManager().createItem(unclaimedItem.getItem(), player, unclaimedItem.getAmount()));
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

	// You can open the inventory with this
    public void openInventory() {
        player.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if(event.getInventory() == inventory) {
			event.setCancelled(true);
		}
	}

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == inventory) {
			InventoryAction action = event.getAction();

			if(action == InventoryAction.CLONE_STACK) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		Bukkit.getLogger().info("InventoryCloseEvent");
		if(event.getInventory() != inventory) {
			return;
		}

		ArrayList<ItemStack> finalContents = new ArrayList<>(Arrays.asList(inventory.getContents()));

		//Mark items that are no longer in inventory as claimed
		Iterator<ItemStack> it = items.iterator();

		while (it.hasNext()) {
			ItemStack item = it.next();
			int index = items.indexOf(item);

			if(!finalContents.contains(item)) {
				Bukkit.getLogger().info("Claiming item " + grantedItems.get(index));
				plugin.getItemManager().claimItem(grantedItems.get(index));
			} else {
				finalContents.remove(item);
			}

			it.remove();
			grantedItems.remove(index);
		}

		//Drop items that shouldn't be in inventory (player added etc)
		for (ItemStack item : finalContents) {
			if(item == null || item.getType() == Material.AIR) {
				continue;
			}

			event.getPlayer().getWorld()
					.dropItemNaturally(plugin.getConfig().getLocation("claimChestLocation",
															event.getPlayer().getLocation()), item);
		}

		HandlerList.unregisterAll(this);
	}
}
