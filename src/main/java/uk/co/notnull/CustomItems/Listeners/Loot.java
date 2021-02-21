package uk.co.notnull.CustomItems.Listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.CustomItems;
import uk.co.notnull.CustomItems.ItemManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Loot implements Listener {
    private final CustomItems plugin;
    private final ItemManager manager;

    public Loot(CustomItems plugin) {
        this.plugin = plugin;
        this.manager = plugin.getItemManager();
    }

    //TODO: Remove this once betterend etc uses loot tables
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        if(block == null) {
            return;
        }

        BlockState state = block.getState();

        if(!(state instanceof Container)) {
            return;
        }

        handleInventory(((Container) block.getState()).getInventory(), event.getPlayer());
    }

    //TODO: Remove this once betterend etc uses loot tables
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();

        if(!(entity instanceof InventoryHolder)) {
            return;
        }

        handleInventory(((InventoryHolder) entity).getInventory(), event.getPlayer());
    }

    //TODO: Remove this once betterend etc uses loot tables
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();

        if(inventory.getType() == InventoryType.ENDER_CHEST) {
            handleInventory(inventory, (Player) event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLootGenerate(LootGenerateEvent event) {
        Player player = (Player) event.getLootContext().getKiller();

        event.setLoot(event.getLoot().stream().map((ItemStack item) -> {
            if(manager.isPlaceholder(item)) {
                return manager.generateLoot(item, player);
            }

            return item;
        }).collect(Collectors.toList()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if(event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            Entity caught = event.getCaught();

            if(caught instanceof Item) {
                if (manager.isPlaceholder(((Item) caught).getItemStack())) {
                    ((Item) caught).setItemStack(manager.generateLoot(((Item) caught).getItemStack(), event.getPlayer()));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDropItem(BlockDropItemEvent event) {
        List<Item> items = event.getItems();

        for (Item item : items) {
            if (manager.isPlaceholder(item.getItemStack())) {
                item.setItemStack(manager.generateLoot(item.getItemStack(), event.getPlayer()));
            }
        }
    }

    private void handleInventory(Inventory inventory, Player player) {
        ItemStack[] items = inventory.getContents();
        plugin.getLogger().info(Arrays.toString(items));

        for(int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

            if(item != null) {
                plugin.getLogger().info(item.toString());
            }

            if(manager.isPlaceholder(item)) {
                inventory.setItem(i, manager.generateLoot(item, player));
            }
        }
    }
}
