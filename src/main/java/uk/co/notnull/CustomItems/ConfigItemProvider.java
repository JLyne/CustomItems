package uk.co.notnull.CustomItems;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
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
	public Collection<CustomItem> provideItems() {
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

			items.put(id, new ConfigCustomItem(id, material, name, components, stamp));
		});
	}
}
