package uk.co.notnull.CustomItems.loot;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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


	public LootManagerImpl(CustomItemsImpl plugin) {
		this.plugin = plugin;
		placeholderPool = new NamespacedKey(plugin, "loot-pool"); //Pool of loot to generate from placeholder item
		placeholderItem = new NamespacedKey(plugin, "loot-item"); //Specific custom item to generate from placeholder item
	}

	public void loadLootConfig(ConfigurationSection config) {
		configuredPools.clear();

		if(config == null) {
			return;
		}

		config.getKeys(false).forEach(pool -> {
			List<NamespacedKey> items = config.getStringList(pool).stream()
					.map(item -> NamespacedKey.fromString(item, plugin))
					.collect(Collectors.toList());
			configuredPools.put(pool, items);
			pools.put(pool, new LootPool(pool));
		});
	}

	public void addItem(CustomItem item) {
		configuredPools.forEach((name, items) -> {
			if(items.contains(item.getId())) {
				pools.get(name).add(item);
			}
		});
	}

	public List<CustomItem> getItems(String pool) {
		if(!pools.containsKey(pool)) {
			throw new IllegalArgumentException("Unknown loot pool " + pool);
		}

		return new ArrayList<>(pools.get(pool));
	}

	public void removeItem(CustomItem item) {
		pools.values().forEach(pool -> pool.remove(item));
	}

	public boolean isPlaceholder(ItemStack item) {
        if(item == null) {
            return false;
        }

        Material material = item.getType();

        if(material != placeholderMaterial) {
			return false;
		}

		item.editPersistentDataContainer(ItemDataManager::updateItemData);
		PersistentDataContainerView data = item.getPersistentDataContainer();

		return data.has(placeholderPool, PersistentDataType.STRING)
				|| data.has(placeholderItem, PersistentDataType.STRING);
	}

	public String getPlaceholderTarget(ItemStack item) {
		if (!isPlaceholder(item)) {
			return null;
		}

		PersistentDataContainerView data = item.getPersistentDataContainer();

		if(data.has(placeholderPool, PersistentDataType.STRING)) {
			return data.get(placeholderPool, PersistentDataType.STRING);
		} else {
			return data.get(placeholderItem, PersistentDataType.STRING);
		}
	}

	public ItemStack generateLoot(ItemStack placeholder, Player player) {
		if (!isPlaceholder(placeholder)) {
			return null;
		}

		PersistentDataContainerView data = placeholder.getPersistentDataContainer();

		if(data.has(placeholderPool, PersistentDataType.STRING)) {
			String pool = data.get(placeholderPool, PersistentDataType.STRING);

			return generateLoot(pool, player, placeholder.getAmount());
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

	public Collection<String> getLootPools() {
		return pools.keySet();
	}

	public LootPool getLootPool(String id) {
		if(!pools.containsKey(id)) {
			throw new IllegalArgumentException("Unknown loot pool " + id);
		}

		return pools.get(id);
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isValidLootPool(String pool) {
		return pools.containsKey(pool);
	}

	@SuppressWarnings("UnusedReturnValue")
	public void givePoolContents(Player player, String pool) {
		if(!isValidLootPool(pool)) {
			throw new IllegalArgumentException("Unknown loot pool " + pool);
		}

		pools.get(pool).giveContents(player);
	}
}
