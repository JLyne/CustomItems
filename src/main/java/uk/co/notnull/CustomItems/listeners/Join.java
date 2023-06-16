package uk.co.notnull.CustomItems.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.messageshelper.Message;

public class Join implements Listener {
    private final CustomItemsImpl plugin;

    public Join(CustomItemsImpl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(plugin.getItemManager().hasUnclaimedItems(player)) {
            plugin.getMessagesHelper().send(player, Message.builder("join.unclaimed-items-available").build());
		}
    }
}
