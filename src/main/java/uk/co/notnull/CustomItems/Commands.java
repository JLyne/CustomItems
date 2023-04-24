package uk.co.notnull.CustomItems;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.notnull.CustomItems.messages.Message;


public class Commands {
    private final CustomItems plugin;
    private final MinecraftHelp<CommandSender> minecraftHelp;

    public Commands(CustomItems plugin, CommandManager<CommandSender> commandManager) {
		this.plugin = plugin;

        this.minecraftHelp = new MinecraftHelp<>("/queue", p -> p, commandManager);
	}

    @CommandMethod("queue help [query]")
    private void commandHelp(
            final CommandSender sender,
            final @Argument("query") @Greedy String query
    ) {
        this.minecraftHelp.queryCommands(query == null ? "" : query, sender);
    }

    @CommandPermission("customitems.give")
    @CommandMethod("customitems give <player> <item> [amount]")
    @CommandDescription("Immediately gives a custom item to a player")
    public void onGiveItem(CommandSender sender, @Argument("player") Player player, @Argument("item") String id,
                           @Argument(value = "amount", defaultValue = "1") int amount) {
        if(!plugin.getItemManager().isValidId(id)) {
            Message.builder("command.invalid-item")
                    .type(Message.MessageType.ERROR)
                    .replacement("item", id)
                    .build().send(sender);
        } else {
            CustomItem item = plugin.getItemManager().getById(id);

            plugin.getItemManager().giveItem(player.getPlayer(), id, amount);
            Message.builder("command.give-success")
                    .replacement("player", player.getPlayer().displayName())
                    .replacement("amount", String.valueOf(amount))
                    .replacement("item", item.getName(player.getPlayer()))
                    .build().send(sender);
        }
    }

    @CommandPermission("customitems.givecategory")
    @CommandMethod("customitems givecategory <player> <category>")
    @CommandDescription("Immediately gives all custom items in a category to a player")
    public void onGiveCategory(CommandSender sender, @Argument("player") Player player, @Argument("category") String category) {
        if(!plugin.getItemManager().isValidCategory(category)) {
            Message.builder("command.invalid-category")
                    .type(Message.MessageType.ERROR)
                    .replacement("category", category)
                    .build().send(sender);
        } else {
            plugin.getItemManager().giveCategory(player.getPlayer(), category);
            Message.builder("command.give-category-success")
                    .replacement("player", player.getPlayer().displayName())
                    .replacement("category", category)
                    .build().send(sender);
        }
    }

    @CommandMethod("customitems grant <player> <item> [amount]")
    @CommandPermission("customitems.grant")
    @CommandDescription("Grants a custom item to a player, which they must collect themselves")
    public void onGrantItem(CommandSender sender, @Argument("player") OfflinePlayer target, @Argument("item") String id,
                            @Argument(value = "amount", defaultValue = "1") int amount) {
        if(!plugin.getItemManager().isValidId(id)) {
            Message.builder("command.invalid-category")
                    .type(Message.MessageType.ERROR)
                    .replacement("item", id)
                    .build().send(sender);
        } else {
            CustomItem item = plugin.getItemManager().getById(id);

            plugin.getItemManager().grantItem(target, id, amount);

            Message.builder("command.grant-success")
                    .replacement("player", target.getName())
                    .replacement("amount", String.valueOf(amount))
                    .replacement("item", item.getName(target.getPlayer()))
                    .build().send(sender);

            if (target.isOnline()) {
                Message.builder("join.unclaimed-items-available").build().send((Player) target);
            }
        }
    }
}
