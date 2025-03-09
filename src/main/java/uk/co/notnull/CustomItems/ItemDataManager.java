package uk.co.notnull.CustomItems;

import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import uk.co.notnull.CustomItems.api.items.CustomItem;

public final class ItemDataManager {
	private static final NamespacedKey customItemKey = new NamespacedKey("customitems", "custom-item"); //Key which identifies a custom item
    private static final NamespacedKey grantedToKey = new NamespacedKey("customitems", "granted-to"); //UUID item was granted to
    private static final NamespacedKey dataVersion = new NamespacedKey("customitems", "version"); //Version of persistant data schema
	private static final Integer currentVersion = 1;

	public static void populateItemData(PersistentDataContainer data, CustomItem item, @Nullable OfflinePlayer granted) {
		data.set(dataVersion, PersistentDataType.INTEGER, 1);
		data.set(customItemKey, PersistentDataType.STRING, item.getId().getKey());

		if(granted != null && item.isStamp()) {
			data.set(grantedToKey, PersistentDataType.STRING, granted.getUniqueId().toString());
		}
	}

	@SuppressWarnings("DataFlowIssue")
	public static void copyItemData(ItemStack oldItem, ItemStack newItem) {
		PersistentDataContainerView oldData = oldItem.getItemMeta().getPersistentDataContainer();

		newItem.editPersistentDataContainer(pdc -> {
			if(oldData.has(customItemKey, PersistentDataType.STRING)) {
				pdc.set(customItemKey, PersistentDataType.STRING, oldData.get(customItemKey, PersistentDataType.STRING));
			}

			if(oldData.has(dataVersion, PersistentDataType.INTEGER)) {
				pdc.set(dataVersion, PersistentDataType.INTEGER, oldData.get(dataVersion, PersistentDataType.INTEGER));
			}

			if(oldData.has(grantedToKey, PersistentDataType.STRING)) {
				pdc.set(grantedToKey, PersistentDataType.STRING, oldData.get(grantedToKey, PersistentDataType.STRING));
			}
		});
	}

	public static NamespacedKey getItemId(PersistentDataContainerView data) {
		if(!data.has(customItemKey, PersistentDataType.STRING)) {
			return null;
		}

		//noinspection DataFlowIssue
		return NamespacedKey.fromString(data.get(customItemKey, PersistentDataType.STRING), CustomItemsImpl.getInstance());
	}

    public static void updateItemData(PersistentDataContainer data) {
        int version = 1;

        if(data.has(dataVersion, PersistentDataType.INTEGER)) {
			//noinspection ConstantConditions
			version = data.get(dataVersion, PersistentDataType.INTEGER);
        }

        version = Math.max(1, version);

        for(int i = version + 1; i <= currentVersion; i++) {
            updateItemDataVersion(data, i);
        }
    }

    @SuppressWarnings("unused")
	private static void updateItemDataVersion(PersistentDataContainer data, int version) {

    }
}
