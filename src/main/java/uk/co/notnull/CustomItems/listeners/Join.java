package uk.co.notnull.CustomItems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.notnull.CustomItems.CustomItemsImpl;

public class Join implements Listener {
    private final CustomItemsImpl plugin;

    public Join(CustomItemsImpl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getItemManager().sendUnclaimedItemsNotification(event.getPlayer());
    }
}
