package uk.co.notnull.CustomItems;

import io.papermc.paper.datacomponent.DataComponentType;
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
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.api.ItemManager;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.api.items.provider.CustomItemProvider;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParser;
import uk.co.notnull.CustomItems.items.ConfigCustomItem;
import uk.co.notnull.CustomItems.items.CreationContextImpl;
import uk.co.notnull.CustomItems.loot.LootManagerImpl;
import uk.co.notnull.messageshelper.Message;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class ItemManagerImpl implements ItemManager {
	private final Map<NamespacedKey, CustomItem> items;
	private final Map<NamespacedKey, CustomItem> externalItems;
	private final Map<UUID, Map<NamespacedKey, Integer>> unclaimed;
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
			Component name = config.getRichMessage(id + ".name");
			ConfigurationSection componentConfig = config.getConfigurationSection(id + ".components");
			boolean stamp = config.getBoolean(id + ".stamp", false);
			Map<DataComponentType, Object> components;

			try {
				components = DataComponentParser.parse(componentConfig, id);
			} catch(RuntimeException e) {
				plugin.getLogger().warning("Failed to parse components for " + id + ": " + e.getMessage());
				return;
			}

			if(materialName == null) {
				plugin.getLogger().warning("No material specified for " + id + ", skipping.");
				return;
			}

			if(name == null) {
				plugin.getLogger().warning("No name specified for " + id + ", skipping.");
				return;
			}

			Material material = Material.matchMaterial(materialName);

			if(material == null) {
				plugin.getLogger().warning("Material " + materialName + " specified for " + id + " does not exist, skipping.");
				return;
			}

			if(!material.isItem()) {
				plugin.getLogger().warning("Material " + materialName + " specified for " + id + " is not an item. Skipping.");
				return;
			}

			if(material.isBlock()) {
				plugin.getLogger().warning("Material " + materialName + " specified for " + id + " is a placeable block. This would cause item data to be lost. Skipping.");
				return;
			}

			addItem(new ConfigCustomItem(id, material, name, components, stamp));
		});
	}

	private void addItem(CustomItem item) {
		if(isValidId(item.getId())) {
			throw new IllegalArgumentException("An item with id " + item.getId() + " is already registered");
		}

		if(!(item instanceof ConfigCustomItem)) {
			externalItems.put(item.getId(), item);
		}

		plugin.getLogger().info("Registered item " + item.getId());

		items.put(item.getId(), item);
		lootManager.addItem(item);
	}

	private void removeItem(CustomItem item) {
		items.remove(item.getId());
		externalItems.remove(item.getId());
		lootManager.removeItem(item);
	}

	public ItemStack createItem(NamespacedKey id, CreationContext context, int amount) {
		CustomItem customItem = getItem(id);

		if(customItem == null) {
			throw new IllegalArgumentException("Unknown item " + id);
		}

		return customItem.createItem(context, amount);
	}

	public void giveItem(Player player, NamespacedKey id, int amount) {
		List<ItemStack> items = new ArrayList<>();
		CreationContext context = new CreationContextImpl(player, CreationReason.GIVEN);

		while(amount > 0) {
        	ItemStack item = createItem(id, context, amount);
			amount -= item.getAmount();
			items.add(item);
		}

        Inventory inventory = player.getInventory();

        final Map<Integer, ItemStack> map = inventory.addItem(items.toArray(new ItemStack[0]));

        if(!map.isEmpty()) {
			map.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
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

		addUnclaimedItem(player, id, amount);
    }

	public void grantItem(OfflinePlayer player, CustomItem item, int amount) {
		if(!isRegistered(item)) {
			throw new IllegalArgumentException("Item id " + item.getId() + " is not registered");
		}

		addUnclaimedItem(player, item.getId(), amount);
	}

	public void grantVanillaItem(OfflinePlayer player, @NotNull NamespacedKey key, int amount) {
        if(!Util.isVanillaItem(key)) {
			throw new IllegalArgumentException(key + " is not a vanilla item");
		}

		addUnclaimedItem(player, key, amount);
    }

	private void addUnclaimedItem(OfflinePlayer player, NamespacedKey id, int amount) {
		// Prevent further claiming of items before grant
		if(player instanceof Player onlinePlayer) {
			chestManager.closeChestClaimGUI(onlinePlayer);
		}

		Map<NamespacedKey, Integer> items = unclaimed.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
		items.compute(id, (key, oldAmount) -> oldAmount != null ? amount + oldAmount : amount);

		if(player instanceof Player onlinePlayer) {
			plugin.getItemManager().sendUnclaimedItemsNotification(onlinePlayer);
		}
	}

	public int revokeItem(OfflinePlayer player, NamespacedKey id) {
		// Prevent further claiming of items before revoke
		if(player instanceof Player onlinePlayer) {
			chestManager.closeChestClaimGUI(onlinePlayer);
		}

       	Map<NamespacedKey, Integer> items = unclaimed.computeIfAbsent(player.getUniqueId(), key -> new HashMap<>());
		Integer amount = items.remove(id);

		return amount != null ? amount : 0;
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

		Collection<CustomItem> items = provider.provideItems();
		items.forEach(this::addItem);

		providers.put(provider, new ArrayList<>(items));
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

	public Map<NamespacedKey, Integer> getUnclaimedItems(@NotNull OfflinePlayer player) {
		return unclaimed.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>()).entrySet().stream()
				.filter(entry -> isValidId(entry.getKey()) || Util.isVanillaItem(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public boolean hasUnclaimedItems(@NotNull OfflinePlayer player) {
		return unclaimed.computeIfAbsent(player.getUniqueId(), uuid -> new HashMap<>()).keySet().stream()
				.anyMatch(key -> isValidId(key) || Util.isVanillaItem(key));
	}

	public void sendUnclaimedItemsNotification(Player player) {
		int amount = getUnclaimedItems(player).values().stream().mapToInt(i -> i).sum();

		if(amount > 0) {
            plugin.getMessagesHelper().send(player, Message.builder("join.unclaimed-items-available")
					.replacement("amount", String.valueOf(amount))
					.build());
		}
	}

	public void claimItem(@NotNull OfflinePlayer player, NamespacedKey item, int amount) {
		Map<NamespacedKey, Integer> playerItems = unclaimed.computeIfAbsent(
				player.getUniqueId(), uuid -> new HashMap<>());

		playerItems.computeIfPresent(item, (key, oldAmount) -> oldAmount - amount <= 0 ? null : oldAmount - amount);
	}

	public void loadUnclaimedItems() {
		unclaimed.clear();

		FileConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "unclaimed.yml"));

		List<?> pending  = data.getList("pending");

		if (pending != null) {
			for (Object item : pending) {
				if(item instanceof GrantedItem grantedItem) {
					Map<NamespacedKey, Integer> playerItems = unclaimed.computeIfAbsent(grantedItem.getPlayer(),
																						key -> new HashMap<>());

					// Add amount to existing GrantedItem if it exists
					playerItems.compute(grantedItem.getItem(), (key, oldAmount) -> {
						if(oldAmount != null) {
							return oldAmount + grantedItem.getAmount();
						}

						return grantedItem.getAmount();
					});
				}
			}
		}
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean saveUnclaimedItems() {
		File unclaimedFile = new File(plugin.getDataFolder(), "unclaimed.yml");
		List<GrantedItem> items = new ArrayList<>();

		unclaimed.forEach((uuid, value) -> value.forEach((id, amount) -> items.add(new GrantedItem(id, amount, uuid))));

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
}
