package uk.co.notnull.CustomItems.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import uk.co.notnull.CustomItems.CustomItem;
import uk.co.notnull.CustomItems.CustomItems;
import uk.co.notnull.CustomItems.Messages;

import java.util.UUID;

@CommandAlias("customitems|ci|citems")
public class CustomItemsCommand extends BaseCommand {
    @Dependency
    private CustomItems plugin;

    @Subcommand("give")
    @Description("Immediately gives a custom item to a player")
    @CommandCompletion("@players @itemids *")
    public void onGiveItem(CommandSender sender, OnlinePlayer player, String id, int amount) {
        if(!plugin.getItemManager().isValidId(id)) {
            plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(), MessageType.ERROR,
                                                   Messages.COMMAND__INVALID_ITEM, "{item}", id);
        } else {
            CustomItem item = plugin.getItemManager().getById(id);

            plugin.getItemManager().giveItem(player.getPlayer(), id, amount);

            plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(),
                                                   MessageType.INFO, Messages.COMMAND__GIVE_SUCCESS,
                                                   "{player}", player.getPlayer().getDisplayName(),
                                                   "{amount}", "" + amount,
                                                   "{item}", item.getName(player.getPlayer()));
        }
    }

    @Subcommand("grant")
    @Description("Grants a custom item to a player, which they must collect themselves")
    @CommandCompletion("* @itemids *")
    public void onGrantItem(CommandSender sender, String uuid, String id, int amount) {
        if(!plugin.getItemManager().isValidId(id)) {
            plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(), MessageType.ERROR,
                                                   Messages.COMMAND__INVALID_ITEM, "{item}", id);
        } else {
            CustomItem item = plugin.getItemManager().getById(id);
            try {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                if(!player.hasPlayedBefore()) {
                    plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(), MessageType.ERROR,
                                                   Messages.COMMAND__INVALID_PLAYER);

                    return;
                }

                plugin.getItemManager().grantItem(player, id, amount);

                plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(),
                                                   MessageType.INFO, Messages.COMMAND__GRANT_SUCCESS,
                                                   "{player}", player.getName(),
                                                   "{amount}", "" + amount,
                                                   "{item}", item.getName(player.getPlayer()));

                if(player.isOnline()) {
                    plugin.getCommandManager().getCommandIssuer(player).sendMessage(MessageType.INFO,
												   Messages.JOIN__UNCLAIMED_ITEMS_AVAILABLE);
                }
            } catch(IllegalArgumentException e) {
                plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(), MessageType.ERROR,
                                                   Messages.COMMAND__INVALID_UUID, "{uuid}", uuid);
            }
        }
    }
}
