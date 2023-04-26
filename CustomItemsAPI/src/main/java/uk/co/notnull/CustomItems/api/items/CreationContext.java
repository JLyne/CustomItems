package uk.co.notnull.CustomItems.api.items;

import org.bukkit.OfflinePlayer;

public interface CreationContext {
	OfflinePlayer player();
	CreationReason reason();
}
