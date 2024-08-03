package uk.co.notnull.CustomItems.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import uk.co.notnull.CustomItems.ChestManager;
import uk.co.notnull.CustomItems.CustomItemsImpl;


public class Inventories implements Listener {
    private final ChestManager manager;

    public Inventories(CustomItemsImpl plugin) {
        this.manager = plugin.getChestManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if(event.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if(!(event.getInventory().getHolder() instanceof BlockInventoryHolder block)) {
            return;
        }

        if(!manager.isChestLocation(block.getBlock().getLocation())) {
            return;
        }

        event.setCancelled(true);
        manager.openChestClaimGUI(player);
    }
}
