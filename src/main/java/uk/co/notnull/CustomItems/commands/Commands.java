package uk.co.notnull.CustomItems.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.captions.CaptionRegistry;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandSuggestionProcessor;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import io.leangen.geantyref.TypeToken;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.messageshelper.Message;
import uk.co.notnull.messageshelper.MessagesHelper;

import java.util.stream.Collectors;


public class Commands {
    private final CustomItemsImpl plugin;
    private final MinecraftHelp<CommandSender> minecraftHelp;
    private final MessagesHelper messagesHelper;

    public Commands(CustomItemsImpl plugin, CommandManager<CommandSender> commandManager) {
		this.plugin = plugin;
        this.minecraftHelp = new MinecraftHelp<>("/queue", p -> p, commandManager);
        this.messagesHelper = plugin.getMessagesHelper();

        commandManager.commandSuggestionProcessor(CommandSuggestionProcessor.passThrough());

        commandManager.parserRegistry().registerSuggestionProvider("lootpools", (
        		CommandContext<CommandSender> commandContext,
                String input
        ) -> plugin.getLootManager().getLootPools().stream()
                .filter(pool -> pool.startsWith(input))
                .collect(Collectors.toList()));

        commandManager.parserRegistry().registerParserSupplier(
                TypeToken.get(CustomItem.class),
                options -> new CustomItemParser<>(plugin));

        final CaptionRegistry<CommandSender> registry = commandManager.captionRegistry();
        if (registry instanceof final FactoryDelegatingCaptionRegistry<CommandSender> factoryRegistry) {
            factoryRegistry.registerMessageFactory(
                    CustomItemParser.ARGUMENT_PARSE_FAILURE_CUSTOM_ITEM,
                    (context, key) -> messagesHelper.getString("command.invalid-item")
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
        messagesHelper.send(sender, Message.builder("command.give-success")
                .prefixed()
                .replacement("player", player.getName())
                .replacement("amount", String.valueOf(amount))
                .replacement("item", item.getDisplayName())
                .build());
    }

    @CommandPermission("customitems.givepool")
    @CommandMethod("customitems givepool <player> <pool>")
    @CommandDescription("Immediately gives all custom items in a loot pool to a player")
    public void onGivePool(CommandSender sender, @Argument("player") Player player,
                           @Argument(value = "pool", suggestions = "lootpools") String pool) {
        if(!plugin.getLootManager().isValidLootPool(pool)) {
            messagesHelper.send(sender, Message.builder("command.invalid-pool")
                    .prefixed()
                    .type(Message.MessageType.ERROR)
                    .replacement("pool", pool)
                    .build());
        } else {
            plugin.getLootManager().givePoolContents(player.getPlayer(), pool);
            messagesHelper.send(sender, Message.builder("command.give-pool-success")
                    .prefixed()
                    .replacement("player", player.getName())
                    .replacement("pool", pool)
                    .build());
        }
    }

    @CommandMethod("customitems grant <player> <item> [amount]")
    @CommandPermission("customitems.grant")
    @CommandDescription("Grants a custom item to a player, which they must collect themselves")
    public void onGrantItem(CommandSender sender, @Argument("player") OfflinePlayer target, @Argument("item") CustomItem item,
                            @Argument(value = "amount", defaultValue = "1") int amount) {
        plugin.getItemManager().grantItem(target, item, amount);

        messagesHelper.send(sender, Message.builder("command.grant-success")
                .prefixed()
                .replacement("player", target.getName())
                .replacement("amount", String.valueOf(amount))
                .replacement("item", item.getDisplayName())
                .build());

        if (target.isOnline()) {
            messagesHelper.send((Player) target, Message.builder("join.unclaimed-items-available").build());
        }
    }
}
