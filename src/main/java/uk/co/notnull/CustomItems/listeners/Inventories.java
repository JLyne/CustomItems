package uk.co.notnull.CustomItems.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.ChestManager;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.api.ItemManager;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.items.ConfigCustomItem;
import uk.co.notnull.CustomItems.items.CreationContextImpl;


public class Inventories implements Listener {
    private final ChestManager chestManager;
    private final ItemManager itemManager;

    public Inventories(CustomItemsImpl plugin) {
        this.chestManager = plugin.getChestManager();
        this.itemManager = plugin.getItemManager();
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

        if(!chestManager.isChestLocation(block.getBlock().getLocation())) {
            return;
        }

        event.setCancelled(true);
        chestManager.openChestClaimGUI(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCreative(InventoryCreativeEvent event) {
        ItemStack item = event.getCursor();
        CustomItem customItem = itemManager.getItem(item);

        if(!(customItem instanceof ConfigCustomItem configItem)) {
            return;
        }

        event.setCursor(configItem.createItem(
                new CreationContextImpl((Player) event.getWhoClicked(), CreationReason.CREATIVE)));
    }
}
