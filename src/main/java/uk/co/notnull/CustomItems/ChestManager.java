package uk.co.notnull.CustomItems;

import org.bukkit.Location;
import org.bukkit.block.Lidded;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChestManager {
	private Location chestLocation;
	private final CustomItems plugin;

	public ChestManager(CustomItems plugin, ConfigurationSection config) {
		this.plugin = plugin;
		chestLocation = config.getLocation("claimChestLocation");

		if(chestLocation != null && !(chestLocation.getBlock().getState() instanceof Lidded)) {
			chestLocation = null;
		}
	}

	public void openChest() {
		if(chestLocation != null && chestLocation.getBlock().getState() instanceof Lidded lidded && !lidded.isOpen()) {
			lidded.open();
		}
	}

	public void closeChest() {
		if(chestLocation != null && chestLocation.getBlock().getState() instanceof Lidded lidded && lidded.isOpen()) {
			lidded.close();
		}
	}

	public void showClaimGUI(Player player) {
		List<GrantedItem> unclaimedItems = plugin.itemManager.getUnclaimedItems(player);

		ClaimGui gui = new ClaimGui(plugin, player, unclaimedItems);
		gui.openInventory();
	}

	public boolean isChestLocation(@NotNull Location location) {
		return location.equals(chestLocation);
	}
}
