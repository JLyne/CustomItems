package uk.co.notnull.CustomItems.Listeners;

import org.bukkit.GameMode;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.CustomItems;
import uk.co.notnull.CustomItems.ItemManager;

public class Wearables implements Listener {
    private final ItemManager manager;

    public Wearables(CustomItems plugin) {
        this.manager = plugin.getItemManager();
    }

    @EventHandler()
    public void onInventoryClick(InventoryClickEvent event) {
        if(!event.getInventory().getType().equals(InventoryType.CRAFTING)) {
            return;
        }

        if(event.getWhoClicked().getEquipment() == null) {
            return;
        }

        InventoryAction action = event.getAction();
        InventoryType.SlotType slotType = event.getSlotType();
        ItemStack equip;

        switch (action) {
            case PLACE_ALL, PLACE_ONE, PLACE_SOME -> {
                //Helmet slot only
                if (event.getRawSlot() != 5 || event.getCursor() == null) {
                    return;
                }

                //Wearables only of course
                if (!manager.isWearable(event.getCursor())) {
                    return;
                }
                event.setCancelled(true);

                //Equip item
                equip = event.getCursor().clone();
                equip.setAmount(1);
                event.getWhoClicked().getEquipment().setHelmet(equip);

                //Reduce cursor by 1 if multiple, otherwise remove
                if (event.getCursor().getAmount() > 1) {
                    event.getCursor().setAmount(event.getCursor().getAmount() - 1);
                } else {
                    //noinspection deprecation
                    event.setCursor(null);
                }
                ((Player) event.getWhoClicked()).updateInventory();
            }
            case SWAP_WITH_CURSOR, NOTHING -> { //Needed for swapping in armor slots it seems
                //Helmet slot only
                if (event.getRawSlot() != 5 || event.getCursor() == null) {
                    return;
                }

                //Wearables only of course
                if (!manager.isWearable(event.getCursor())) {
                    return;
                }

                //Don't allow swaps if stack is on cursor
                if (event.getCursor().getAmount() > 1) {
                    return;
                }

                //Swap items
                event.setCancelled(true);
                equip = event.getCursor().clone();
                //noinspection deprecation
                event.setCursor(event.getCurrentItem());
                event.getWhoClicked().getEquipment().setHelmet(equip);
            }
            case MOVE_TO_OTHER_INVENTORY -> {
                //Ignore shift clicks slots outside of actual inv (i.e crafting area)
                if (!slotType.equals(InventoryType.SlotType.CONTAINER)
                        && !slotType.equals(InventoryType.SlotType.QUICKBAR)) {
                    return;
                }

                //Ignore shift clicks if destination slot is already full
                if (event.getWhoClicked().getEquipment().getHelmet() != null) {
                    return;
                }

                //Ignore shift clicks for stacked items (like beacon)
                if (event.getCurrentItem() == null || event.getCurrentItem().getAmount() > 1) {
                    return;
                }

                //Ignore non wearables of course
                if (!manager.isWearable(event.getCurrentItem())) {
                    return;
                }

                //Move to helmet slot
                event.setCancelled(true);
                event.getWhoClicked().getEquipment().setHelmet(event.getCurrentItem());
                event.setCurrentItem(null);
            }
        }
    }

    @EventHandler()
    public void onItemUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getHand() == null || event.getItem() == null || !event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        if(!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if(event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Container) {
            return;
        }

        EntityEquipment equipment = player.getEquipment();

        if(manager.isWearable(event.getItem()) && equipment != null && equipment.getHelmet() == null) {
            ItemStack mainHand = equipment.getItemInMainHand();

            ItemStack equip = mainHand.clone();
            equip.setAmount(1);
            equipment.setHelmet(equip);

            if(!player.getGameMode().equals(GameMode.CREATIVE)) {
                if(mainHand.getAmount() > 1) {
                    mainHand.setAmount(mainHand.getAmount() - 1);
                } else {
                    equipment.setItemInMainHand(null);
                }
            }
        }
    }
}
