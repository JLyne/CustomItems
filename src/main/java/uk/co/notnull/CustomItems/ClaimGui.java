package uk.co.notnull.CustomItems;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.items.CreationContextImpl;

import java.util.*;

public final class ClaimGui implements InventoryHolder, Listener {
	private final CustomItemsImpl plugin;
	private final Inventory inventory = Bukkit.getServer().createInventory(this, InventoryType.CHEST);
	private final Map<ItemStack, NamespacedKey> itemMapping = new HashMap<>();
	private final List<ItemStack> initialContents = new ArrayList<>();
	private final ItemManagerImpl itemManager;
	private final OfflinePlayer player;

	public ClaimGui(CustomItemsImpl plugin, OfflinePlayer player) {
		this.player = player;
		this.plugin = plugin;
		itemManager = plugin.getItemManager();

		updateContents();
	}

	public void updateContents() {
		Map<NamespacedKey, Integer> unclaimedItems = itemManager.getUnclaimedItems(player);
		CreationContext context = new CreationContextImpl(player, CreationReason.GRANTED);
		itemMapping.clear();
		initialContents.clear();

		for (Map.Entry<NamespacedKey, Integer> entry : unclaimedItems.entrySet()) {
			NamespacedKey id = entry.getKey();
			Integer amount = entry.getValue();
			CustomItem customItem = Util.isVanillaItem(id) ? Util.getVanillaCustomItem(id) : itemManager.getItem(id);

			while (amount > 0) {
				if(initialContents.size() == inventory.getSize()) {
					break;
				}

				ItemStack item = customItem.createItem(context, amount);
				amount -= item.getAmount();
				itemMapping.put(item, id);
				initialContents.add(item);
			}
		}

		ItemStack[] contents = new ItemStack[initialContents.size()];
		initialContents.toArray(contents);
		inventory.setContents(contents);
	}

	public void claimItems() {
		ArrayList<ItemStack> finalContents = new ArrayList<>(Arrays.asList(inventory.getContents()));

		//Mark unclaimed items that are no longer in inventory as claimed
		for (ItemStack item : initialContents) {
			if (!finalContents.contains(item)) {
				NamespacedKey id = itemMapping.get(item);
				plugin.getLogger().info("Claiming item " + id + "x" + item.getAmount() + " for " + player.getName());
				itemManager.claimItem(player, id, item.getAmount());
			} else {
				finalContents.remove(item);
			}
		}

		ItemStack[] contents = new ItemStack[finalContents.size()];
		finalContents.toArray(contents);
		inventory.setContents(contents);
	}

	@NotNull
	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}
}
