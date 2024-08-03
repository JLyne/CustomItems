package uk.co.notnull.CustomItems;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import uk.co.notnull.CustomItems.api.ItemManager;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.api.items.CustomItemProvider;
import uk.co.notnull.CustomItems.items.ConfigCustomItem;
import uk.co.notnull.CustomItems.items.CreationContextImpl;
import uk.co.notnull.CustomItems.loot.LootManagerImpl;
import uk.co.notnull.messageshelper.MessagesHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class ItemManagerImpl implements ItemManager {
	private final Map<NamespacedKey, CustomItem> items;
	private final Map<NamespacedKey, CustomItem> externalItems;
	private final Map<UUID, Map<NamespacedKey, GrantedItem>> unclaimed;
	private final Map<CustomItemProvider, List<CustomItem>> providers;

	private final CustomItemsImpl plugin;
	private final LootManagerImpl lootManager;
	private final ChestManager chestManager;

	public ItemManagerImpl(CustomItemsImpl plugin, LootManagerImpl lootManager, ChestManager chestManager) {
		this.plugin = plugin;
		this.lootManager = lootManager;
		this.chestManager = chestManager;

		items = new HashMap<>();
		externalItems = new HashMap<>();
		unclaimed = new HashMap<>();
		providers = new HashMap<>();

		loadUnclaimedItems();
	}

	public void loadItemConfig(ConfigurationSection config) {
		items.clear();
		items.putAll(externalItems);

		if(config == null) {
			return;
		}

		config.getKeys(false).forEach(id -> {
			String materialName = config.getString(id + ".material");
			int model = config.getInt(id + ".custom-model-data", 0);
			Component name = MessagesHelper.miniMessage.deserialize(config.getString(id + ".name", ""));
			List<String> lore = config.getStringList(id + ".lore");
			boolean wearable = config.getBoolean(id + ".wearable", false);
			boolean stamp = config.getBoolean(id + ".stamp", false);

			if(materialName == null) {
				plugin.getLogger().warning("No material specified for " + id + ", skipping.");
				return;
			}

			Material material = Material.getMaterial(materialName);

			if(material == null) {
				plugin.getLogger().warning("Material " + materialName + " specified for " + id + " does not exist, skipping.");
				return;
			}

			if(material.isBlock()) {
				plugin.getLogger().warning("Material " + materialName + " specified for " + id + " is a placeable block. This would cause item data to be lost. Skipping.");
				return;
			}

			addItem(new ConfigCustomItem(id, material, model, name, lore, wearable, stamp));
		});
	}

	private void addItem(CustomItem item) {
		if(isValidId(item.getId())) {
			throw new IllegalArgumentException("An item with id " + item.getId() + " is already registered");
		}

		if(!(item instanceof ConfigCustomItem)) {
			externalItems.put(item.getId(), item);
		}

		items.put(item.getId(), item);
		lootManager.addItem(item);
	}

	private void removeItem(CustomItem item) {
		items.remove(item.getId());
		externalItems.remove(item.getId());
		lootManager.removeItem(item);
	}

	public ItemStack createItem(NamespacedKey id, CreationContext context, int amount) {
		if(!isValidId(id)) {
			throw new IllegalArgumentException("Item id " + id + " is not registered");
		}

		return items.get(id).createItem(context, amount);
	}

	public void giveItem(Player player, NamespacedKey id, int amount) {
        ItemStack item = createItem(id, new CreationContextImpl(player, CreationReason.GIVEN), amount);

        Inventory inventory = player.getInventory();

        final Map<Integer, ItemStack> map = inventory.addItem(item);

        if(!map.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

	public void giveItem(Player player, CustomItem item, int amount) {
		if(!isRegistered(item)) {
			throw new IllegalArgumentException("Item id " + item.getId() + " is not registered");
		}

		giveItem(player, item.getId(), amount);
	}

	public void grantItem(OfflinePlayer player, NamespacedKey id, int amount) {
		if(!isValidId(id)) {
			throw new IllegalArgumentException("Unknown item " + id);
		}

		// Prevent further claiming of items before grant
		if(player instanceof Player onlinePlayer) {
			chestManager.closeChestClaimGUI(onlinePlayer);
		}

        Map<NamespacedKey, GrantedItem> items = unclaimed.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());

		items.compute(id, (key, item) -> {
			int finalAmount = item != null ? amount + item.getAmount() : amount;
			return new GrantedItem(id, finalAmount, player.getUniqueId());
		});
        unclaimed.put(player.getUniqueId(), items);
    }

	public void grantItem(OfflinePlayer player, CustomItem item, int amount) {
		if(!isRegistered(item)) {
			throw new IllegalArgumentException("Item id " + item.getId() + " is not registered");
		}

		grantItem(player, item.getId(), amount);
	}

	public int revokeItem(OfflinePlayer player, NamespacedKey id) {
		if(!isValidId(id)) {
			throw new IllegalArgumentException("Unknown item " + id);
		}

		// Prevent further claiming of items before revoke
		if(player instanceof Player onlinePlayer) {
			chestManager.closeChestClaimGUI(onlinePlayer);
		}

		AtomicInteger amount = new AtomicInteger(0);

       	Map<NamespacedKey, GrantedItem> items = unclaimed.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());

		items = items.values().stream().filter(item -> {
			if(item.getItem().equals(id)) {
				amount.addAndGet(item.getAmount());
				return false;
			} else {
				return true;
			}
		}).collect(Collectors.toMap(GrantedItem::getItem, grantedItem -> grantedItem));

        unclaimed.put(player.getUniqueId(), items);
		return amount.get();
    }

	public int revokeItem(OfflinePlayer player, CustomItem item) {
		if(!isRegistered(item)) {
			throw new IllegalArgumentException("Item id " + item.getId() + " is not registered");
		}

		return revokeItem(player, item.getId());
	}

    public Set<NamespacedKey> getItemIds() {
		return items.keySet();
	}

	public boolean isValidId(NamespacedKey id) {
		return items.containsKey(id);
	}

	public boolean isRegistered(CustomItem item) {
		return items.containsValue(item);
	}

	public void registerProvider(CustomItemProvider provider) {
		if(providers.containsKey(provider)) {
			throw new IllegalArgumentException("Provider already registered");
		}

		List<CustomItem> items = provider.provideItems();
		items.forEach(this::addItem);

		providers.put(provider, items);
	}

	public void unregisterProvider(CustomItemProvider provider) {
		if(!providers.containsKey(provider)) {
			return;
		}

		providers.remove(provider).forEach(this::removeItem);
	}

	public CustomItem getItem(NamespacedKey id) {
		return items.get(id);
	}

	public CustomItem getItem(ItemStack item) {
		if(item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		ItemDataManager.updateItemData(data);
		item.setItemMeta(meta);

		NamespacedKey id = ItemDataManager.getItemId(data);

		if(id != null) {
			return items.get(id);
		}

		for(CustomItemProvider provider: providers.keySet()) {
			CustomItem identified = provider.identifyItem(item);

			if(identified != null) {
				return identified;
			}
		}

		return null;
	}

	public List<GrantedItem> getUnclaimedItems(OfflinePlayer player) {
		return unclaimed.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>()).values().stream()
				.filter(item -> isValidId(item.getItem())).collect(Collectors.toList());
	}

	public boolean hasUnclaimedItems(OfflinePlayer player) {
		return unclaimed.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>()).values().stream()
				.anyMatch(item -> isValidId(item.getItem()));
	}

	public void claimItem(GrantedItem item) {
		unclaimed.computeIfAbsent(item.getPlayer(), uuid -> new HashMap<>()).remove(item.getItem());
	}

	public void loadUnclaimedItems() {
		unclaimed.clear();

		FileConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "unclaimed.yml"));

		List<?> pending  = data.getList("pending");

		if (pending != null) {
			for (Object item : pending) {
				if(item instanceof GrantedItem grantedItem) {
					Map<NamespacedKey, GrantedItem> playerItems = unclaimed.computeIfAbsent(grantedItem.getPlayer(),
																						key -> new HashMap<>());

					// Add amount to existing GrantedItem if it exists
					playerItems.compute(grantedItem.getItem(), (key, value) -> {
						if(value != null) {
							return new GrantedItem(key, value.getAmount() + grantedItem.getAmount(), grantedItem.getPlayer());
						}

						return grantedItem;
					});
				}
			}
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean saveUnclaimedItems() {
		File unclaimedFile = new File(plugin.getDataFolder(), "unclaimed.yml");
		List<GrantedItem> items = new ArrayList<>();

		unclaimed.values().stream().map(Map::values).forEach(items::addAll);

		FileConfiguration data = new YamlConfiguration();

		data.set("pending", items);

		try {
			data.save(unclaimedFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isWearable(ItemStack item) {
		CustomItem customItem = getItem(item);

		return customItem != null && customItem.isWearable();
	}
}
