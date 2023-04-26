package uk.co.notnull.CustomItems.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.loot.LootManagerImpl;

import java.util.List;
import java.util.stream.Collectors;

public class Loot implements Listener {
    private final LootManagerImpl manager;

    public Loot(CustomItemsImpl plugin) {
        this.manager = plugin.getLootManager();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();

        if(inventory.isEmpty()) {
            return;
        }

        handleInventory(inventory, (Player) event.getPlayer());
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

        for(int i = 0; i < items.length; i++) {
            ItemStack item = items[i];

            if(manager.isPlaceholder(item)) {
                inventory.setItem(i, manager.generateLoot(item, player));
            }
        }
    }
}
