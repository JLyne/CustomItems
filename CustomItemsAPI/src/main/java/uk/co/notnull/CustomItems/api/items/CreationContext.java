package uk.co.notnull.CustomItems.api.items;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CreationContext {
	@Nullable OfflinePlayer player();
	@NotNull CreationReason reason();
}
