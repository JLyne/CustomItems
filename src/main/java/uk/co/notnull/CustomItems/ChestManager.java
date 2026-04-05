package uk.co.notnull.CustomItems;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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
	private final Map<Player, ClaimGui> openChestGUIs = new HashMap<>(); // GUIs opened by interacting with the chest
	private final Map<OfflinePlayer, ClaimGui> openCommandGUIs = new HashMap<>(); // GUIs opened via /customitems:viewunclaimed <player>

	public ChestManager(CustomItemsImpl plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void setChestLocation(Location location) {
		if(location != null && location.getBlock().getState() instanceof Lidded) {
			chestLocation = location;
		}
	}

	public void openChestClaimGUI(Player player) {
		ClaimGui gui = new ClaimGui(plugin, player);
		player.openInventory(gui.getInventory());
		openChestGUIs.put(player, gui);
		openChest();
	}

	public void closeChestClaimGUI(Player player) {
		if(openChestGUIs.containsKey(player)) {
			player.closeInventory();
		}
	}

	public void showCommandClaimGUI(Player viewer, OfflinePlayer target) {
		ClaimGui gui = new ClaimGui(plugin, target);
		viewer.openInventory(gui.getInventory());
		openCommandGUIs.put(target, gui);
	}

	public void closeCommandClaimGUI(OfflinePlayer target) {
		if(openCommandGUIs.containsKey(target)) {
			openCommandGUIs.get(target).getInventory().getViewers().forEach(HumanEntity::closeInventory);
		}
	}

	public void closeAllGUIS() {
		openCommandGUIs.values().forEach(gui -> gui.getInventory().close());
		openChestGUIs.values().forEach(gui -> gui.getInventory().close());
		closeChest();
	}

	// Prevent dragging items in claim guis
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getInventory().getHolder(false) instanceof ClaimGui) {
			event.setCancelled(true);
		}
	}

	// Prevent cloning items in claim guis, or any interaction at all for command-created guis
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inventory = event.getClickedInventory();

		if(inventory == null || !(inventory.getHolder(false) instanceof ClaimGui gui)) {
			return;
		}

		// Ignore players viewing via /customitems:viewunclaimed
		if(!gui.equals(openChestGUIs.get((Player) event.getWhoClicked()))) {
			event.setCancelled(true);
			return;
		}

		InventoryAction action = event.getAction();

		if(action == InventoryAction.CLONE_STACK) {
			event.setCancelled(true);
		}
	}

	// Update unclaimed items when chest-created gui is closed
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		Inventory inventory = event.getInventory();
		Player player = (Player) event.getPlayer();

		if(!(inventory.getHolder(false) instanceof ClaimGui claimGui)) {
			return;
		}

		// Ignore players viewing via /customitems:viewunclaimed
		if(!claimGui.equals(openChestGUIs.get(player))) {
			openCommandGUIs.remove(player);
			return;
		}

		//Mark unclaimed items that are no longer in inventory as claimed
		claimGui.claimItems();

		//Drop items that shouldn't be in inventory (player added etc.)
		for (ItemStack item : inventory.getContents()) {
			if(item == null || item.isEmpty()) {
				continue;
			}

			player.getWorld().dropItemNaturally(chestLocation != null ? chestLocation : player.getLocation(), item);
		}

		openChestGUIs.remove(player);

		// Update any command inventories for this player
		if(openCommandGUIs.containsKey(player)) {
			openCommandGUIs.get(player).updateContents();
		}

		if(openChestGUIs.isEmpty()) {
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
