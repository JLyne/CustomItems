package uk.co.notnull.CustomItems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Lidded;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ChestManager implements Listener {
	private Location chestLocation;
	private final CustomItemsImpl plugin;
	private final Map<UUID, ClaimGui> openClaimGuis = new HashMap<>();

	public ChestManager(CustomItemsImpl plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void setChestLocation(Location location) {
		if(location != null && location.getBlock().getState() instanceof Lidded) {
			chestLocation = location;
		}
	}

	public void showClaimGUI(Player player) {
		List<GrantedItem> unclaimedItems = plugin.itemManager.getUnclaimedItems(player);

		ClaimGui gui = new ClaimGui(plugin, player, unclaimedItems);
		player.openInventory(gui.getInventory());
		openClaimGuis.put(player.getUniqueId(), gui);
		openChest();
	}

	public void closeAllGUIS() {
		openClaimGuis.forEach((player, gui) -> {
			gui.getInventory().getViewers().forEach(HumanEntity::closeInventory);
		});
		closeChest();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getInventory().getHolder(false) instanceof ClaimGui) {
			event.setCancelled(true);
		}
	}

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getClickedInventory();

		if(inventory == null || !(inventory.getHolder(false) instanceof ClaimGui)) {
			return;
		}

		InventoryAction action = event.getAction();

		if(action == InventoryAction.CLONE_STACK) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inventory = event.getInventory();
		if(!(inventory.getHolder(false) instanceof ClaimGui claimGui)) {
			return;
		}

		ArrayList<ItemStack> finalContents = new ArrayList<>(Arrays.asList(inventory.getContents()));

		//Mark items that are no longer in inventory as claimed
		List<ItemStack> items = claimGui.getItems();
		List<GrantedItem> grantedItems = claimGui.getGrantedItems();
		Iterator<ItemStack> it = items.iterator();

		while (it.hasNext()) {
			ItemStack item = it.next();
			int index = items.indexOf(item);

			if(!finalContents.contains(item)) {
				plugin.getLogger().info("Claiming item " + grantedItems.get(index));
				plugin.itemManager.claimItem(grantedItems.get(index));
			} else {
				finalContents.remove(item);
			}

			it.remove();
			grantedItems.remove(index);
		}

		//Drop items that shouldn't be in inventory (player added etc.)
		for (ItemStack item : finalContents) {
			if(item == null || item.getType() == Material.AIR) {
				continue;
			}

			event.getPlayer().getWorld()
					.dropItemNaturally(chestLocation != null ? chestLocation : event.getPlayer().getLocation(), item);
		}

		openClaimGuis.remove(event.getPlayer().getUniqueId());

		if(openClaimGuis.isEmpty()) {
			closeChest();
		}
	}

	public boolean isChestLocation(@NotNull Location location) {
		return location.equals(chestLocation);
	}

	private void openChest() {
		if(chestLocation != null && chestLocation.getBlock().getState() instanceof Lidded lidded && !lidded.isOpen()) {
			lidded.open();
		}
	}

	private void closeChest() {
		if(chestLocation != null && chestLocation.getBlock().getState() instanceof Lidded lidded && lidded.isOpen()) {
			lidded.close();
		}
	}
}
