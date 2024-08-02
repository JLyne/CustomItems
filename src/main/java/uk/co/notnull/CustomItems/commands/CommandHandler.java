package uk.co.notnull.CustomItems.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.loot.LootPool;
import uk.co.notnull.messageshelper.Message;
import uk.co.notnull.messageshelper.MessagesHelper;

import java.util.List;


@SuppressWarnings("UnstableApiUsage")
public class CommandHandler {
    private final CustomItemsImpl plugin;
    private final MessagesHelper messagesHelper;

    private final CustomItemArgumentType itemArgumentType;
    private final LootPoolArgumentType lootPoolArgumentType;
    private final IntegerArgumentType amountArgumentType = IntegerArgumentType.integer(0, 6400);
    private final OfflinePlayerArgumentType offlinePlayerArgumentType;

    public CommandHandler(CustomItemsImpl plugin, Commands commandManager) {
        this.plugin = plugin;
        this.messagesHelper = plugin.getMessagesHelper();
        this.itemArgumentType = new CustomItemArgumentType(plugin);
        this.lootPoolArgumentType = new LootPoolArgumentType(plugin);
        this.offlinePlayerArgumentType = new OfflinePlayerArgumentType(plugin);

        commandManager.register(this.createGiveItem(), "Immediately gives a custom item to a player");
        commandManager.register(this.createGrantItem(), "Grants a custom item to a player, which they must collect themselves");
        commandManager.register(this.createGivePool(), "Immediately gives all custom items in a loot pool to a player");
        commandManager.register(this.createReload(), "Reloads the config");
    }

    private LiteralCommandNode<CommandSourceStack> createGiveItem() {
        return Commands.literal("giveitem")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("customitems.give"))
                .then(Commands.argument("player", ArgumentTypes.players())
                              .then(Commands.argument("item", itemArgumentType).executes(ctx -> {
                                  onGiveItem(ctx.getSource(),
                                             ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                             ctx.getArgument("item", CustomItem.class), 1);
                                  return Command.SINGLE_SUCCESS;
                              }).then(Commands.argument("amount", amountArgumentType).executes(ctx -> {
                                  onGiveItem(ctx.getSource(),
                                             ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                             ctx.getArgument("item", CustomItem.class),
                                             ctx.getArgument("amount", Integer.class));
                                  return Command.SINGLE_SUCCESS;
                              }))))
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createGrantItem() {
        // customitems:grantitem <player> <item> [amount]
        RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> onlinePlayer =
                Commands.argument("player", ArgumentTypes.players())
                        .then(Commands.argument("item", itemArgumentType).executes(ctx -> {
                            onGrantItem(ctx.getSource(),
                                        ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                        ctx.getArgument("item", CustomItem.class), 1);
                            return Command.SINGLE_SUCCESS;
                        }).then(Commands.argument("amount", amountArgumentType).executes(ctx -> {
                            onGrantItem(ctx.getSource(),
                                        ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                        ctx.getArgument("item", CustomItem.class),
                                        ctx.getArgument("amount", Integer.class));
                            return Command.SINGLE_SUCCESS;
                        })));

        // customitems:grantitem offline <offline-player> <item> [amount]
        LiteralArgumentBuilder<CommandSourceStack> offlinePlayer =
                Commands.literal("offline").then(
                    Commands.argument("offline-player", offlinePlayerArgumentType)
                            .then(Commands.argument("item", itemArgumentType).executes(ctx -> {
                                 onGrantItem(ctx.getSource(),
                                             ctx.getArgument("offline-player", OfflinePlayer.class),
                                             ctx.getArgument("item", CustomItem.class), 1);
                                 return Command.SINGLE_SUCCESS;
                            }).then(Commands.argument("amount", amountArgumentType).executes(ctx -> {
                                onGrantItem(ctx.getSource(),
                                            ctx.getArgument("offline-player", OfflinePlayer.class),
                                            ctx.getArgument("item", CustomItem.class),
                                            ctx.getArgument("amount", Integer.class));
                                return Command.SINGLE_SUCCESS;
                            }))));

        return Commands.literal("grantitem")
                .requires(commandSourceStack ->
                                  commandSourceStack.getSender().hasPermission("customitems.grant"))
                .then(onlinePlayer)
                .then(offlinePlayer)
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createGivePool() {
        return Commands.literal("givepool")
                .requires(commandSourceStack ->
                                  commandSourceStack.getSender().hasPermission("customitems.givepool"))
                .then(Commands.argument("player", ArgumentTypes.players())
                              .then(Commands.argument("pool", lootPoolArgumentType).executes(ctx -> {
                                  onGivePool(ctx.getSource(),
                                             ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                             ctx.getArgument("pool", LootPool.class));
                                  return Command.SINGLE_SUCCESS;
                              })))
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createReload() {
        return Commands.literal("reload")
                .requires(commandSourceStack ->
                                  commandSourceStack.getSender().hasPermission("customitems.reload"))
                .executes(ctx -> {
                    onReload(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    private void onGiveItem(CommandSourceStack source, PlayerSelectorArgumentResolver target, CustomItem item, int amount) throws CommandSyntaxException {
        List<Player> players = target.resolve(source);

        for (Player player : players) {
            plugin.getItemManager().giveItem(player, item, amount);

            messagesHelper.send(source.getSender(), Message.builder("command.give-success")
                    .prefixed()
                    .replacement("player", player.displayName())
                    .replacement("amount", String.valueOf(amount))
                    .replacement("item", item.getDisplayName())
                    .build());
        }
    }

    // customitems:grantitem <player> <item> [amount]
    private void onGrantItem(CommandSourceStack source, PlayerSelectorArgumentResolver target, CustomItem item, int amount) throws CommandSyntaxException {
        List<Player> players = target.resolve(source);

        for (Player player : players) {
            onGrantItem(source, player, item, amount);
        }
    }

    // customitems:grantitem <offline-player> <item> [amount]
    private void onGrantItem(CommandSourceStack source, OfflinePlayer target, CustomItem item, int amount) {
        plugin.getItemManager().grantItem(target, item, amount);

        messagesHelper.send(source.getSender(), Message.builder("command.grant-success")
                .prefixed()
                .replacement("player", target.getName() != null ? target.getName() : target.getUniqueId().toString())
                .replacement("amount", String.valueOf(amount))
                .replacement("item", item.getDisplayName())
                .build());

        if(target.isConnected()) {
            messagesHelper.send((Player) target, Message.builder("join.unclaimed-items-available").build());
        }
    }

    private void onGivePool(CommandSourceStack source, PlayerSelectorArgumentResolver target, LootPool pool) throws CommandSyntaxException {
		List<Player> players = target.resolve(source);

        for(Player player: players) {
            pool.giveContents(player);
            messagesHelper.send(source.getSender(), Message.builder("command.give-pool-success")
                    .prefixed()
                    .replacement("player", player.getName())
                    .replacement("pool", pool.getName())
                    .build());
        }
	}

    private void onReload(CommandSourceStack source) {
        try {
		    plugin.initConfig();
            messagesHelper.send(source.getSender(), Message.builder("command.reload-success").prefixed().build());
        } catch(Exception ex) {
            plugin.getLogger().warning("Error while reloading config" + ex.getMessage());
            messagesHelper.send(source.getSender(), Message.builder("command.reload-error")
                    .prefixed()
                    .type(Message.MessageType.ERROR)
                    .build());
        }
	}
}
