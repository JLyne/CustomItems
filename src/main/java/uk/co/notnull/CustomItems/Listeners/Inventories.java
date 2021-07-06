package uk.co.notnull.CustomItems.Listeners;

import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import uk.co.notnull.CustomItems.CustomItems;
import uk.co.notnull.CustomItems.ItemManager;


public class Inventories implements Listener {
    private final ItemManager manager;
    private final Location chestLocation;

    public Inventories(CustomItems plugin) {
        this.manager = plugin.getItemManager();
        this.chestLocation = plugin.getConfig().getLocation("claimChestLocation");
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if(event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if(!(event.getInventory().getHolder() instanceof Container)) {
            return;
        }

        Location blockLocation = ((Container) event.getInventory().getHolder()).getLocation();

        if(chestLocation == null || !chestLocation.equals(blockLocation)) {
            return;
        }

        event.setCancelled(true);
        manager.showClaimGUI(player);
    }
}
