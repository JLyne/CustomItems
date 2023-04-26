package uk.co.notnull.CustomItems.items;

import org.bukkit.OfflinePlayer;
import uk.co.notnull.CustomItems.api.items.CreationContext;
import uk.co.notnull.CustomItems.api.items.CreationReason;

public record CreationContextImpl(OfflinePlayer player, CreationReason reason) implements CreationContext {

}
