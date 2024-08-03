package uk.co.notnull.CustomItems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.api.ItemManager;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.items.CreationContextImpl;

import java.util.ArrayList;
import java.util.List;

public final class ClaimGui implements InventoryHolder, Listener {
	private final Inventory inventory;
	private final ArrayList<ItemStack> items;
	private List<GrantedItem> unclaimedItems;
	private final ItemManager itemManager;
	private final OfflinePlayer player;

	public ClaimGui(CustomItemsImpl plugin, OfflinePlayer player, List<GrantedItem> unclaimedItems) {
		this.unclaimedItems = unclaimedItems;
		this.player = player;

		itemManager = plugin.getItemManager();
		inventory = Bukkit.getServer().createInventory(this, InventoryType.CHEST);
		items = new ArrayList<>();

		updateContents();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void updateContents() {
		CreationContext context = new CreationContextImpl(player, CreationReason.GRANTED);
		items.clear();

		for (GrantedItem unclaimedItem : unclaimedItems) {
			if(itemManager.isValidId(unclaimedItem.getItem())) {
				items.add(itemManager.createItem(unclaimedItem.getItem(), context, unclaimedItem.getAmount()));
			} else {
				ItemStack air = new ItemStack(Material.AIR, 1);
				items.add(air);
			}
		}

		ItemStack[] contents = new ItemStack[items.size()];
		items.toArray(contents);
		inventory.setContents(contents);
	}

	@NotNull
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public void setUnclaimedItems(List<GrantedItem> unclaimedItems) {
		this.unclaimedItems = unclaimedItems;
		updateContents();
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public List<GrantedItem> getUnclaimedItems() {
		return unclaimedItems;
	}

	public List<ItemStack> getItems() {
		return items;
	}
}
