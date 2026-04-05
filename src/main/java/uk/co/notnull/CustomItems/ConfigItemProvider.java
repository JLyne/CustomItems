package uk.co.notnull.CustomItems;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.api.items.provider.CustomItemProvider;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParser;
import uk.co.notnull.CustomItems.items.ConfigCustomItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ConfigItemProvider implements CustomItemProvider {
	private final CustomItemsImpl plugin;
	private final Map<String, CustomItem> items = new HashMap<>();

	ConfigItemProvider(CustomItemsImpl plugin) {
		this.plugin = plugin;
	}

	@Override
	public @NotNull JavaPlugin getPlugin() {
		return plugin;
	}

	@Override
	public @NotNull Collection<CustomItem> provideItems() {
		return items.values();
	}

	@Override
	public @Nullable CustomItem identifyItem(ItemStack item) {
		item.editPersistentDataContainer(ItemDataManager::updateItemData);
		PersistentDataContainerView data = item.getPersistentDataContainer();

		NamespacedKey id = ItemDataManager.getItemId(data);

		if(id != null) {
			return items.get(id.getKey());
		}

		return null;
	}

	@SuppressWarnings("UnstableApiUsage")
	void loadItemConfig(ConfigurationSection config) {
		items.clear();

		if(config == null) {
			return;
		}

		config.getKeys(false).forEach(id -> {
			String typeName = config.getString(id + ".type", config.getString(id + ".material"));
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

			if(typeName == null) {
				plugin.getLogger().warning("No item type specified for " + id + ", skipping.");
				return;
			}

			NamespacedKey typeKey = NamespacedKey.fromString(typeName);

			if(typeKey == null) {
				plugin.getLogger().warning("Invalid item type specified for " + id + ", skipping.");
				return;
			}

			if(name == null) {
				plugin.getLogger().warning("No name specified for " + id + ", skipping.");
				return;
			}

			ItemType itemType = Registry.ITEM.get(typeKey);

			if(itemType == null) {
				plugin.getLogger().warning("Unknown item type " + typeName + " specified for " + id + ", skipping.");
				return;
			}

			items.put(id, new ConfigCustomItem(id, itemType, name, components, stamp));
		});
	}
}
