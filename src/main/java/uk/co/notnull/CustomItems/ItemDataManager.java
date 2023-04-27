package uk.co.notnull.CustomItems;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
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

	public static NamespacedKey getItemId(PersistentDataContainer data) {
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
