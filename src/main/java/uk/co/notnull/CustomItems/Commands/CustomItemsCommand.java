package uk.co.notnull.CustomItems.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
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

    @CommandPermission("customitems.give")
    @Subcommand("give")
    @Description("Immediately gives a custom item to a player")
    @CommandCompletion("@players @itemids *")
    public void onGiveItem(CommandSender sender, OnlinePlayer player, String id, @Default("1") int amount) {
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

    @CommandPermission("customitems.givecategory")
    @Subcommand("givecategory")
    @Description("Immediately gives all custom items in a category to a player")
    @CommandCompletion("@players @categories")
    public void onGiveCategory(CommandSender sender, OnlinePlayer player, String category) {
        if(!plugin.getItemManager().isValidCategory(category)) {
            plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(), MessageType.ERROR,
                                                   Messages.COMMAND__INVALID_CATEGORY, "{item}", category);
        } else {
            plugin.getItemManager().giveCategory(player.getPlayer(), category);
            plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(),
                                                   MessageType.INFO, Messages.COMMAND__GIVE_CATEGORY_SUCCESS,
                                                   "{player}", player.getPlayer().getDisplayName(),
                                                   "{category}", category);
        }
    }

    @Subcommand("grant")
    @CommandPermission("customitems.grant")
    @Description("Grants a custom item to a player, which they must collect themselves")
    @CommandCompletion("@players @itemids *")
    public void onGrantItem(CommandSender sender, OfflinePlayer target, String id, @Default("1") int amount) {
        if(!plugin.getItemManager().isValidId(id)) {
            plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(), MessageType.ERROR,
                                                   Messages.COMMAND__INVALID_ITEM, "{item}", id);
        } else {
            CustomItem item = plugin.getItemManager().getById(id);

            plugin.getItemManager().grantItem(target, id, amount);

            plugin.getCommandManager().sendMessage(getCurrentCommandIssuer(),
                                                   MessageType.INFO, Messages.COMMAND__GRANT_SUCCESS,
                                                   "{player}", target.getName(),
                                                   "{amount}", "" + amount,
                                                   "{item}", item.getName(target.getPlayer()));

            if (target.isOnline()) {
                plugin.getCommandManager().getCommandIssuer(target).sendMessage(MessageType.INFO,
                                                                                Messages.JOIN__UNCLAIMED_ITEMS_AVAILABLE);
            }
        }
    }
}
