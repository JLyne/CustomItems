package uk.co.notnull.CustomItems.loot;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.ItemDataManager;
import uk.co.notnull.CustomItems.api.items.CreationReason;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.api.loot.LootManager;
import uk.co.notnull.CustomItems.items.CreationContextImpl;

import java.util.*;
import java.util.stream.Collectors;

public class LootManagerImpl implements LootManager {

	private final CustomItemsImpl plugin;
	private final NamespacedKey placeholderPool;

    private final NamespacedKey placeholderItem;

    private static final Material placeholderMaterial = Material.SEA_PICKLE;

	private final Map<String, LootPool> pools = new HashMap<>();

	private final Map<String, List<NamespacedKey>> configuredPools = new HashMap<>();


	public LootManagerImpl(CustomItemsImpl plugin, ConfigurationSection lootConfig) {
		this.plugin = plugin;
		placeholderPool = new NamespacedKey(plugin, "loot-pool"); //Pool of loot to generate from placeholder item
		placeholderItem = new NamespacedKey(plugin, "loot-item"); //Specific custom item to generate from placeholder item

		loadLootConfig(lootConfig);
	}

	public void loadLootConfig(ConfigurationSection config) {
		configuredPools.clear();

		config.getKeys(false).forEach(pool -> {
			List<NamespacedKey> items = config.getStringList(pool).stream()
					.map(item -> NamespacedKey.fromString(item, plugin))
					.collect(Collectors.toList());
			configuredPools.put(pool, items);
			pools.put(pool, new LootPool());
		});
	}

	public void addItem(CustomItem item) {
		configuredPools.forEach((name, items) -> {
			if(items.contains(item.getId())) {
				pools.get(name).add(item);
			}
		});
	}

	public void removeItem(CustomItem item) {
		pools.values().forEach(pool -> pool.remove(item));
	}

	public boolean isPlaceholder(ItemStack item) {
        if(item == null || !item.hasItemMeta()) {
            return false;
        }

        Material material = item.getType();

        if(material != placeholderMaterial) {
			return false;
		}

        ItemMeta meta = item.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		ItemDataManager.updateItemData(data);
		item.setItemMeta(meta);

		return data.has(placeholderPool, PersistentDataType.STRING)
				|| data.has(placeholderItem, PersistentDataType.STRING);
	}

	public ItemStack generateLoot(ItemStack placeholder, Player player) {
		if (!isPlaceholder(placeholder)) {
			return null;
		}

		ItemMeta meta = placeholder.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		if(data.has(placeholderPool, PersistentDataType.STRING)) {
			String category = data.get(placeholderPool, PersistentDataType.STRING);

			return generateLoot(category, player, placeholder.getAmount());
		} else {
			NamespacedKey item = NamespacedKey.fromString(data.get(placeholderItem, PersistentDataType.STRING), plugin);

			if(!plugin.getItemManager().isValidId(item)) {
				return null;
			}

			return plugin.getItemManager()
					.createItem(item, new CreationContextImpl(player, CreationReason.LOOT), placeholder.getAmount());
		}
	}

	public ItemStack generateLoot(String pool, Player player, int amount) {
		if (pool == null || !pools.containsKey(pool)) {
			return null;
		}

		return pools.get(pool).generateItem(player, amount);
	}

	public Collection<String> getCategories() {
		return pools.keySet();
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isValidCategory(String category) {
		return pools.containsKey(category);
	}
}
