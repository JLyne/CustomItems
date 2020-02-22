package uk.co.notnull.CustomItems;

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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ItemManager {
	private NamespacedKey customItemKey;
    private NamespacedKey grantedToKey;
    private NamespacedKey dataVersion;

	Map<String, CustomItem> items;
	Map<UUID, List<GrantedItem>> unclaimed;

	private CustomItems plugin;

	public ItemManager(CustomItems plugin, ConfigurationSection config) {
		this.plugin = plugin;

		items = new HashMap<>();
		unclaimed = new HashMap<>();

		customItemKey = new NamespacedKey(plugin, "custom-item"); //Key which identifies a custom item
        grantedToKey = new NamespacedKey(plugin, "granted-to"); //UUID item was granted to
        dataVersion = new NamespacedKey(plugin, "version"); //Version of persistant data schema

		loadItemConfig(config);
		loadUnclaimedItems();
	}

	public void loadItemConfig(ConfigurationSection config) {
		items.clear();

		config.getKeys(false).forEach(id -> {
			String item = config.getString(id + ".item");
			int damage = config.getInt(id + ".damage", 0);
			String name = config.getString(id + ".name");
			List<String> lore = config.getStringList(id + ".lore");

			if(item == null) {
				plugin.getLogger().severe("No item specified for " + id + ", skipping.");
				return;
			}

			Material material = Material.getMaterial(item);

			if(material == null) {
				plugin.getLogger().severe("Item " + item + " specified for " + id + " does not exist, skipping.");
				return;
			}

			items.put(id, new CustomItem(id, material, damage, name, lore));
		});
	}

	public ItemStack createItem(String id, OfflinePlayer player, int amount) {
		if(!isValidId(id)) {
			plugin.getLogger().warning("Refusing to create invalid item " + id);

			return null;
		}

		CustomItem customItem = items.get(id);

		ItemStack item = new ItemStack(customItem.getItem(), amount);
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);

		if(meta instanceof Damageable) {
			((Damageable) meta).setDamage(customItem.getDamage());
		}

		meta.getPersistentDataContainer().set(dataVersion, PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(customItemKey, PersistentDataType.STRING, id);

		if(player != null) {
			meta.getPersistentDataContainer().set(grantedToKey, PersistentDataType.STRING, player.getUniqueId().toString());
		}

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		meta.setDisplayName(customItem.getName(player));
		meta.setLore(customItem.getLore(player));
		item.setItemMeta(meta);

		return item;
	}

	public boolean giveItem(Player player, String id, int amount) {
		if(!isValidId(id)) {
			plugin.getLogger().warning("Refusing to give invalid item " + id);

			return false;
		}

        ItemStack part = createItem(id, player, amount);
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

    public void showClaimGUI(Player player) {
		List<GrantedItem> unclaimedItems = getUnclaimedItems(player);

		ClaimGui gui = new ClaimGui(plugin, player, unclaimedItems);
		gui.openInventory();
	}

    public Set<String> getItemIds() {
		return items.keySet();
	}

	public boolean isValidId(String id) {
		return items.containsKey(id);
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
			plugin.getLogger().info(pending.toString());

			for (Object item : pending) {
				items.add((GrantedItem) item);
			}
		} else {
			plugin.getLogger().warning("Pending is null");
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
}
