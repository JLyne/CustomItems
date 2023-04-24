package uk.co.notnull.CustomItems.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.notnull.CustomItems.CustomItems;
import uk.co.notnull.CustomItems.messages.Message;

public class Join implements Listener {
    private final CustomItems plugin;

    public Join(CustomItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(plugin.getItemManager().hasUnclaimedItems(player)) {
            Message.builder("join.unclaimed-items-available").build().send(player);
		}
    }
}
