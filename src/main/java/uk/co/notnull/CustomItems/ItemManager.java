package uk.co.notnull.CustomItems;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import uk.co.notnull.CustomItems.tags.LootTierTag;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ItemManager {
	private final NamespacedKey customItemKey;
    private final NamespacedKey grantedToKey;
    private final NamespacedKey dataVersion;

    private final NamespacedKey placeholderTier;
    private final NamespacedKey placeholderCategory;

    private final LootTierTag lootTierTag = new LootTierTag();
    private static final Material placeholderMaterial = Material.SEA_PICKLE;
    private static final Integer currentVersion = 1;
    private final Random random = new Random();

	Map<String, CustomItem> items;
	Map<UUID, List<GrantedItem>> unclaimed;
	Table<String, LootTier, List<CustomItem>> loot;

	private final CustomItems plugin;

	public ItemManager(CustomItems plugin, ConfigurationSection config) {
		this.plugin = plugin;

		items = new HashMap<>();
		unclaimed = new HashMap<>();
		loot = HashBasedTable.create();

		customItemKey = new NamespacedKey(plugin, "custom-item"); //Key which identifies a custom item
        grantedToKey = new NamespacedKey(plugin, "granted-to"); //UUID item was granted to
        dataVersion = new NamespacedKey(plugin, "version"); //Version of persistant data schema

		placeholderTier = new NamespacedKey(plugin, "loot-tier"); //Tier of loot to generate from placeholder item
		placeholderCategory = new NamespacedKey(plugin, "loot-category"); //Category of loot to generate from placeholder item

		loadItemConfig(config);
		loadUnclaimedItems();
	}

	public void loadItemConfig(ConfigurationSection config) {
		items.clear();

		config.getKeys(false).forEach(id -> {
			String materialName = config.getString(id + ".material");
			int model = config.getInt(id + ".custom-model-data", 0);
			String name = config.getString(id + ".name");
			List<String> lore = config.getStringList(id + ".lore");
			boolean wearable = config.getBoolean(id + ".wearable", false);

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

			ConfigurationSection lootConfig = config.getConfigurationSection(id + ".loot");

			if(lootConfig != null) {
				String category = lootConfig.getString("category", "none");
				LootTier tier = LootTier.valueOf(lootConfig.getInt("tier", 1));
				boolean stamp = lootConfig.getBoolean("stamp", false);

				if(tier.equals(LootTier.INVALID)) {
					plugin.getLogger().warning("Invalid loot tier specified for " + id + ", skipping.");
					return;
				}

				CustomItem item = new CustomItem(id, material, model, name, lore, wearable, category, tier, stamp);
				items.put(id, item);
				addLoot(item);
			} else {
				items.put(id, new CustomItem(id, material, model, name, lore, wearable));
			}
		});
	}

	private void addLoot(CustomItem item) {
		if(!item.isLoot()) {
			return;
		}

		String category = item.getLootCategory();
		LootTier tier = item.getLootTier();

		List<CustomItem> loot = this.loot.contains(category, tier) ? this.loot.get(category, tier) : new ArrayList<>();
		loot.add(item);
		this.loot.put(category, tier, loot);
	}

	public ItemStack createItem(String id, OfflinePlayer player) {
		return createItem(id, player, 1);
	}

	public ItemStack createItem(String id, OfflinePlayer player, int amount) {
		if(!isValidId(id)) {
			plugin.getLogger().warning("Refusing to create invalid item " + id);

			return null;
		}

		return createItem(items.get(id), player, amount);
	}

	public ItemStack createItem(CustomItem customItem, OfflinePlayer player) {
		return createItem(customItem, player, 1);
	}

	public ItemStack createItem(CustomItem customItem, OfflinePlayer player, int amount) {
		ItemStack item = new ItemStack(customItem.getItem(), amount);
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);

		meta.setCustomModelData(customItem.getModel());
		meta.getPersistentDataContainer().set(dataVersion, PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(customItemKey, PersistentDataType.STRING, customItem.getId());

		if(player != null && customItem.isStamp()) {
			meta.getPersistentDataContainer().set(grantedToKey, PersistentDataType.STRING, player.getUniqueId().toString());
		}

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		meta.displayName(customItem.getDisplayName(player));
		meta.lore(customItem.getLore(player));
		item.setItemMeta(meta);

		return item;
	}

	public boolean giveItem(Player player, String id, int amount) {
        ItemStack part = createItem(id, player, amount);

        if(part == null) {
        	return false;
		}

        Inventory inventory = player.getInventory();

        final Map<Integer, ItemStack> map = inventory.addItem(part);

        if(!map.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), part);
        }

        return true;
    }

    public boolean grantItem(OfflinePlayer player, String id, int amount) {
		if(!isValidId(id)) {
			plugin.getLogger().warning("Refusing to grant invalid item " + id);

			return false;
		}

        List<GrantedItem> items = unclaimed.getOrDefault(player.getUniqueId(), new ArrayList<>());

        items.add(new GrantedItem(id, amount, player.getUniqueId()));
        unclaimed.put(player.getUniqueId(), items);

        return true;
    }

    public boolean giveCategory(Player player, String category) {
		if(!isValidCategory(category)) {
			plugin.getLogger().warning("Refusing to give invalid category " + category);

			return false;
		}

		List<ItemStack> created = new ArrayList<>();

		this.items.values().forEach((CustomItem item) -> {
			if(item.isLoot() && item.getLootCategory().equals(category)) {
				created.add(createItem(item, player));
			}
		});

		Inventory inventory = player.getInventory();

        final Map<Integer, ItemStack> map = inventory.addItem(created.toArray(new ItemStack[0]));

        map.values().forEach((ItemStack item) -> player.getWorld().dropItemNaturally(player.getLocation(), item));

        return true;
	}

    public void showClaimGUI(Player player) {
		List<GrantedItem> unclaimedItems = getUnclaimedItems(player);

		ClaimGui gui = new ClaimGui(plugin, player, unclaimedItems);
		gui.openInventory();
	}

    public Set<String> getItemIds() {
		return items.keySet();
	}

	public Collection<String> getCategories() {
		return loot.rowKeySet();
	}

	public boolean isValidId(String id) {
		return items.containsKey(id);
	}

	public boolean isValidCategory(String category) {
		return loot.containsRow(category);
	}

	public CustomItem getById(String id) {
		return items.get(id);
	}

	public List<GrantedItem> getUnclaimedItems(OfflinePlayer player) {
		return unclaimed.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>()).stream()
				.filter(item -> isValidId(item.getItem())).collect(Collectors.toList());
	}

	public boolean hasUnclaimedItems(OfflinePlayer player) {
		return unclaimed.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>()).stream()
				.anyMatch(item -> isValidId(item.getItem()));
	}

	public void claimItem(GrantedItem item) {
		unclaimed.computeIfAbsent(item.getPlayer(), uuid -> new ArrayList<>()).remove(item);
	}

	public void loadUnclaimedItems() {
		unclaimed.clear();

		FileConfiguration data = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "unclaimed.yml"));

		List<?> pending  = data.getList("pending");
		ArrayList<GrantedItem> items = new ArrayList<>();

		if (pending != null) {
			for (Object item : pending) {
				items.add((GrantedItem) item);
			}
		}

		unclaimed = items.stream().collect(Collectors.groupingBy(GrantedItem::getPlayer));
	}

	public boolean saveUnclaimedItems() {
		File unclaimedFile = new File(plugin.getDataFolder(), "unclaimed.yml");
		List<GrantedItem> items = unclaimed.values().stream()
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

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

		if(itemDataNeedsUpdate(data)) {
			updateItemData(data);
			item.setItemMeta(meta);
		}

		if(!data.has(placeholderTier, lootTierTag)) {
			return false;
		}

		LootTier tier = data.get(placeholderTier, lootTierTag);

		return tier != null && !tier.equals(LootTier.INVALID);
	}

	public boolean isWearable(ItemStack item) {
		CustomItem customItem = getCustomItem(item);

		return customItem != null && customItem.isWearable();
	}

	public CustomItem getCustomItem(ItemStack item) {
		if(item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		if(itemDataNeedsUpdate(data)) {
			updateItemData(data);
			item.setItemMeta(meta);
		}

		if(!data.has(customItemKey, PersistentDataType.STRING)) {
			return null;
		}

		return items.get(data.get(customItemKey, PersistentDataType.STRING));
	}

    public ItemStack generateLoot(ItemStack placeholder, Player player) {
		if(!isPlaceholder(placeholder)) {
			return null;
		}

		ItemMeta meta = placeholder.getItemMeta();
		PersistentDataContainer data = meta.getPersistentDataContainer();

		LootTier tier = data.get(placeholderTier, lootTierTag);
		String category = data.get(placeholderCategory, PersistentDataType.STRING);

		if(category == null) {
			category = "none";
		}

		if(!loot.contains(category, tier)) {
			return null;
		}

		List<CustomItem> pool = loot.get(category, tier);

		if(pool.isEmpty()) {
			return null;
		}

		CustomItem item = pool.get(random.nextInt(pool.size()));

		return createItem(item, player, placeholder.getAmount());
	}

    private Boolean itemDataNeedsUpdate(PersistentDataContainer data) {
        int version = 1;

        if(data.isEmpty()) {
            return false;
        }

        if(data.has(dataVersion, PersistentDataType.INTEGER)) {
            version = data.get(dataVersion, PersistentDataType.INTEGER);
        }

        return version < currentVersion;
    }

    private void updateItemData(PersistentDataContainer data) {
        int version = 1;

        if(data.has(dataVersion, PersistentDataType.INTEGER)) {
            version = data.get(dataVersion, PersistentDataType.INTEGER);
        }

        version = Math.max(1, version);

        for(int i = version + 1; i <= currentVersion; i++) {
            updateItemDataVersion(data, i);
        }
    }

    @SuppressWarnings({"unchecked", "SwitchStatementWithTooFewBranches"})
    private void updateItemDataVersion(PersistentDataContainer data, int version) {

    }
}
