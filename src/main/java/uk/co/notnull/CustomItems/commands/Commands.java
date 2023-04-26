package uk.co.notnull.CustomItems.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.captions.CaptionRegistry;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import io.leangen.geantyref.TypeToken;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.notnull.CustomItems.api.CustomItems;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.messages.Message;
import uk.co.notnull.CustomItems.messages.Messages;


public class Commands {
    private final CustomItems plugin;
    private final MinecraftHelp<CommandSender> minecraftHelp;

    public Commands(CustomItems plugin, CommandManager<CommandSender> commandManager) {
		this.plugin = plugin;
        this.minecraftHelp = new MinecraftHelp<>("/queue", p -> p, commandManager);

        commandManager.parserRegistry().registerParserSupplier(
                TypeToken.get(CustomItem.class),
                options -> new CustomItemParser<>(plugin));

        final CaptionRegistry<CommandSender> registry = commandManager.captionRegistry();
        if (registry instanceof final FactoryDelegatingCaptionRegistry<CommandSender> factoryRegistry) {
            factoryRegistry.registerMessageFactory(
                    CustomItemParser.ARGUMENT_PARSE_FAILURE_CUSTOM_ITEM,
                    (context, key) -> Messages.get("command.invalid-item")
            );
        }
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
    public void onGiveItem(CommandSender sender, @Argument("player") Player player, @Argument("item") CustomItem item,
                           @Argument(value = "amount", defaultValue = "1") int amount) {
        plugin.getItemManager().giveItem(player.getPlayer(), item, amount);
        Message.builder("command.give-success")
                .prefixed()
                .replacement("player", player.getName())
                .replacement("amount", String.valueOf(amount))
                .replacement("item", item.getDisplayName())
                .build().send(sender);
    }

    @CommandPermission("customitems.givecategory")
    @CommandMethod("customitems givecategory <player> <category>")
    @CommandDescription("Immediately gives all custom items in a category to a player")
    public void onGiveCategory(CommandSender sender, @Argument("player") Player player, @Argument("category") String category) {
        if(!plugin.getLootManager().isValidCategory(category)) {
            Message.builder("command.invalid-category")
                    .prefixed()
                    .type(Message.MessageType.ERROR)
                    .replacement("category", category)
                    .build().send(sender);
        } else {
            plugin.getItemManager().giveCategory(player.getPlayer(), category);
            Message.builder("command.give-category-success")
                    .prefixed()
                    .replacement("player", player.getName())
                    .replacement("category", category)
                    .build().send(sender);
        }
    }

    @CommandMethod("customitems grant <player> <item> [amount]")
    @CommandPermission("customitems.grant")
    @CommandDescription("Grants a custom item to a player, which they must collect themselves")
    public void onGrantItem(CommandSender sender, @Argument("player") OfflinePlayer target, @Argument("item") CustomItem item,
                            @Argument(value = "amount", defaultValue = "1") int amount) {
        plugin.getItemManager().grantItem(target, item, amount);

        Message.builder("command.grant-success")
                .prefixed()
                .replacement("player", target.getName())
                .replacement("amount", String.valueOf(amount))
                .replacement("item", item.getDisplayName())
                .build().send(sender);

        if (target.isOnline()) {
            Message.builder("join.unclaimed-items-available").build().send((Player) target);
        }
    }
}
