package uk.co.notnull.CustomItems.items;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CreationReason;

public record CreationContextImpl(@Nullable OfflinePlayer player, @NotNull CreationReason reason) implements CreationContext {

}
