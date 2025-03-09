package uk.co.notnull.CustomItems.items;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.ItemDataManager;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.AbstractCustomItem;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public final class ConfigCustomItem extends AbstractCustomItem {
	private final Material item;
	private final Map<DataComponentType, Object> components;
	private final Set<DataComponentType> componentTypes;

	public ConfigCustomItem(String id, Material item, Component name, Map<DataComponentType, Object> components, boolean stamp) {
		super(new NamespacedKey(CustomItemsImpl.getInstance(), id), name, stamp);
		this.item = item;
		this.components = components;
		this.componentTypes = Collections.unmodifiableSet(components.keySet());
	}

	public Material getItem() {
		return item;
	}

	@Override
	public String toString() {
		return "CustomItem{" +
				"id='" + id + '\'' +
				", displayName='" + displayName + '\'' +
				", item=" + item +
				", stamp=" + stamp +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigCustomItem that = (ConfigCustomItem) o;
		return isStamp() == that.isStamp() && Objects.equals(
						getId(), that.getId()) && getItem() == that.getItem() &&
						Objects.equals(getDisplayName(), that.getDisplayName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getItem(), getDisplayName(), isStamp());
	}

	public Set<DataComponentType> getComponentTypes() {
		return componentTypes;
	}

	@Override
	public ItemStack createItem(CreationContext context, int amount) {
		ItemStack item = new ItemStack(getItem(), amount);
		item.editPersistentDataContainer(
				data -> ItemDataManager.populateItemData(data, this, context.player()));

		TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
				.matchLiteral("<player>")
				.replacement(context.player().getName())
				.build();

		if(components.containsKey(DataComponentTypes.LORE)) {
			ItemLore lore = (ItemLore) components.get(DataComponentTypes.LORE);
			List<Component> replacedLines = lore.lines().stream()
					.map(l -> l.replaceText(replacementConfig)).toList();
			ItemLore replaced = ItemLore.lore().lines(replacedLines).build();

			components.put(DataComponentTypes.LORE, replaced);
		}

		if(components.containsKey(DataComponentTypes.ITEM_NAME)) {
			Component itemName = (Component) components.get(DataComponentTypes.ITEM_NAME);
			Component replacedName = itemName.replaceText(replacementConfig);

			components.put(DataComponentTypes.ITEM_NAME, replacedName);
		} else {
			components.put(DataComponentTypes.ITEM_NAME, displayName);
		}

		components.forEach((type, value) -> {
			if(type instanceof DataComponentType.Valued valued) {
				item.setData(valued, value);
			} else if (type instanceof DataComponentType.NonValued nonvalued) {
				if((boolean) value) {
					item.setData(nonvalued);
				} else {
					item.unsetData(nonvalued); //TODO: Useful?
				}
			}
		});

		item.setAmount(Math.min(item.getMaxStackSize(), amount));

		return item;
	}
}

