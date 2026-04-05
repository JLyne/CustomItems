package uk.co.notnull.CustomItems.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.Util;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.CustomItems.loot.LootPool;
import uk.co.notnull.messageshelper.Message;
import uk.co.notnull.messageshelper.MessagesHelper;

import java.util.List;
import java.util.logging.Level;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.players;


public class CommandHandler {
    private final CustomItemsImpl plugin;
    private final MessagesHelper messagesHelper;

    private final CustomItemArgumentType itemArgumentType;
    private final VanillaItemArgumentType vanillaItemArgumentType;
    private final LootPoolArgumentType lootPoolArgumentType;
    private final ProviderPluginArgumentType providerPluginArgumentType;
    private final IntegerArgumentType amountArgumentType = integer(0, 6400);
    private final OfflinePlayerArgumentType offlinePlayerArgumentType;

    public CommandHandler(CustomItemsImpl plugin, Commands commandManager) {
        this.plugin = plugin;
        this.messagesHelper = plugin.getMessagesHelper();
        this.itemArgumentType = new CustomItemArgumentType(plugin);
        this.vanillaItemArgumentType = new VanillaItemArgumentType(plugin);
        this.lootPoolArgumentType = new LootPoolArgumentType(plugin);
        this.providerPluginArgumentType = new ProviderPluginArgumentType(plugin);
        this.offlinePlayerArgumentType = new OfflinePlayerArgumentType(plugin);

        commandManager.register(this.createGiveItem(), "Immediately gives a custom item to a player");
        commandManager.register(this.createGrantItem(), "Grants a custom item to a player, which they must collect themselves");
        commandManager.register(this.createGivePool(), "Immediately gives all custom items in a loot pool to a player");
        commandManager.register(this.createGivePlugin(), "Immediately gives all custom items provided by a plugin to a player");
        commandManager.register(this.createRevokeItem(), "Revokes previously granted unclaimed items from a player");
        commandManager.register(this.createViewUnclaimed(), "View the unclaimed items for a player");
        commandManager.register(this.createReload(), "Reloads the config");
    }

    private LiteralCommandNode<CommandSourceStack> createGiveItem() {
        return literal("giveitem")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("customitems.give"))
                .then(argument("player", players())
                              .then(argument("item", itemArgumentType).executes(ctx -> {
                                  onGiveItem(ctx.getSource(),
                                             ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                             ctx.getArgument("item", NamespacedKey.class), 1);
                                  return Command.SINGLE_SUCCESS;
                              }).then(argument("amount", amountArgumentType).executes(ctx -> {
                                  onGiveItem(ctx.getSource(),
                                             ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                             ctx.getArgument("item", NamespacedKey.class),
                                             ctx.getArgument("amount", Integer.class));
                                  return Command.SINGLE_SUCCESS;
                              }))))
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createGrantItem() {
        Command<CommandSourceStack> noAmountExecutor = (CommandContext<CommandSourceStack> ctx) -> {
            onGrantItem(ctx.getSource(),
                        ctx.getArgument("player", Object.class),
                        ctx.getArgument("item", NamespacedKey.class), 1);

            return Command.SINGLE_SUCCESS;
        };

        Command<CommandSourceStack> withAmountExecutor = (CommandContext<CommandSourceStack> ctx) -> {
            onGrantItem(ctx.getSource(),
                        ctx.getArgument("player", Object.class),
                        ctx.getArgument("item", NamespacedKey.class),
                        ctx.getArgument("amount", Integer.class));

            return Command.SINGLE_SUCCESS;
        };

        // customitems:grantitem <player>
        RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> onlinePlayer =
                argument("player", players())
                        // customitems:grantitem <player> <item> [amount]
                        .then(argument("item", itemArgumentType)
                                      .executes(noAmountExecutor)
                                      .then(argument("amount", amountArgumentType)
                                                    .executes(withAmountExecutor)))
                        // customitems:grantitem <player> vanilla <item> [amount]
                        .then(literal("vanilla")
                                      .then(argument("item", vanillaItemArgumentType)
                                                    .executes(noAmountExecutor)
                                                    .then(argument("amount", amountArgumentType)
                                                                  .executes(withAmountExecutor))));

        // customitems:grantitem offline <player>
        LiteralArgumentBuilder<CommandSourceStack> offlinePlayer =
                literal("offline").then(
                    argument("player", offlinePlayerArgumentType)
                            // customitems:grantitem offline <player> <item> [amount]
                            .then(argument("item", itemArgumentType)
                                          .executes(noAmountExecutor)
                                          .then(argument("amount", amountArgumentType)
                                                        .executes(withAmountExecutor)))
                            // customitems:grantitem offline <player> vanilla <item> [amount]
                            .then(literal("vanilla")
                                          .then(argument("item", vanillaItemArgumentType)
                                                        .executes(noAmountExecutor)
                                                        .then(argument("amount", amountArgumentType)
                                                                      .executes(withAmountExecutor)))));

        return literal("grantitem")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("customitems.grant"))
                .then(onlinePlayer)
                .then(offlinePlayer)
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createGivePool() {
        return literal("givepool")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("customitems.givepool"))
                .then(argument("player", players())
                              .then(argument("pool", lootPoolArgumentType).executes(ctx -> {
                                  onGivePool(ctx.getSource(),
                                             ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                             ctx.getArgument("pool", LootPool.class));
                                  return Command.SINGLE_SUCCESS;
                              })))
                .build();
    }

	private LiteralCommandNode<CommandSourceStack> createGivePlugin() {
        return literal("giveplugin")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("customitems.giveplugin"))
                .then(argument("player", players())
                              .then(argument("plugin", providerPluginArgumentType).executes(ctx -> {
                                  onGivePlugin(ctx.getSource(),
                                             ctx.getArgument("player", PlayerSelectorArgumentResolver.class),
                                             ctx.getArgument("plugin", Plugin.class));
                                  return Command.SINGLE_SUCCESS;
                              })))
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createRevokeItem() {
        Command<CommandSourceStack> executor = (CommandContext<CommandSourceStack> ctx) -> {
            onRevokeItem(ctx.getSource(),
                                        ctx.getArgument("player", Object.class),
                                        ctx.getArgument("item", NamespacedKey.class));

            return Command.SINGLE_SUCCESS;
        };

        // customitems:revokeitem <player>
        RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> onlinePlayer =
                argument("player", players())
                        // customitems:grantitem <player> <item>
                        .then(argument("item", itemArgumentType).executes(executor))
                        // customitems:grantitem <player> vanilla <item>
                        .then(literal("vanilla")
                                      .then(argument("item", vanillaItemArgumentType)
                                                    .executes(executor)));

        // customitems:revokeitem offline <player>
        LiteralArgumentBuilder<CommandSourceStack> offlinePlayer =
                literal("offline").then(
                    argument("player", offlinePlayerArgumentType)
                            // customitems:revokeitem offline <player> <item>
                            .then(argument("item", itemArgumentType).executes(executor))
                            // customitems:grantitem offline <player> vanilla <item>
                            .then(literal("vanilla")
                                          .then(argument("item", vanillaItemArgumentType)
                                                        .executes(executor))));

        return literal("revokeitem")
                .requires(commandSourceStack -> commandSourceStack.getSender().hasPermission("customitems.revoke"))
                .then(onlinePlayer)
                .then(offlinePlayer)
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createViewUnclaimed() {
        return literal("viewunclaimed")
                .requires(commandSourceStack ->
                                  commandSourceStack.getSender() instanceof Player
                                          && commandSourceStack.getSender().hasPermission("customitems.view"))
                // customitems:viewunclaimed <player>
                .then(argument("player", ArgumentTypes.player()).executes(ctx -> {
                    onViewUnclaimed(ctx.getSource(),
                                    ctx.getArgument("player", PlayerSelectorArgumentResolver.class));
                    return Command.SINGLE_SUCCESS;
                }))
                // customitems:viewunclaimed offline <offline-player>
                .then(literal("offline")
                              .then(argument("offline-player", offlinePlayerArgumentType).executes(ctx -> {
                                  onViewUnclaimed(ctx.getSource(),
                                                  ctx.getArgument("offline-player", OfflinePlayer.class));
                                  return Command.SINGLE_SUCCESS;
                              })))
                .build();
    }

    private LiteralCommandNode<CommandSourceStack> createReload() {
        return literal("reload")
                .requires(commandSourceStack ->
                                  commandSourceStack.getSender().hasPermission("customitems.reload"))
                .executes(ctx -> {
                    onReload(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }

    private void onGiveItem(CommandSourceStack source, PlayerSelectorArgumentResolver target, NamespacedKey key, int amount) throws CommandSyntaxException {
        List<Player> players = target.resolve(source);
        CustomItem item = plugin.getItemManager().getItem(key);

        if (players.isEmpty()) {
            source.getSender()
                .sendMessage(Component.translatable("argument.entity.notfound.player")
                    .color(NamedTextColor.RED));

            return;
        }

        for (Player player : players) {
            plugin.getItemManager().giveItem(player, item, amount);

            messagesHelper.send(source.getSender(), Message.builder("command.give-success")
                    .prefixed()
                    .replacement("player", player.displayName())
                    .replacement("amount", String.valueOf(amount))
                    .replacement("item", item.getDisplayName()) //TODO: Replace with item preview
                    .build());
        }
    }

    private void onGrantItem(CommandSourceStack source, Object target, NamespacedKey key, int amount) throws CommandSyntaxException {
        if(target instanceof OfflinePlayer offlinePlayer) {
             onGrantItem(source, offlinePlayer, key, amount);
        } else if(target instanceof PlayerSelectorArgumentResolver selector) {
            List<Player> players = selector.resolve(source);

            if (players.isEmpty()) {
                source.getSender()
                        .sendMessage(Component.translatable("argument.entity.notfound.player")
                                             .color(NamedTextColor.RED));

                return;
            }

            for (Player player : players) {
                onGrantItem(source, player, key, amount);
            }
        } else {
            throw new IllegalArgumentException("target must be an OfflinePlayer or PlayerSelectorArgumentResolver");
        }
    }

    private void onGrantItem(CommandSourceStack source, OfflinePlayer target, NamespacedKey key, int amount) {
        CustomItem item;

        if(Util.isVanillaItem(key)) {
            item = Util.getVanillaCustomItem(key);
            plugin.getItemManager().grantVanillaItem(target, key, amount);
        } else {
            plugin.getItemManager().grantItem(target, key, amount);
            item = plugin.getItemManager().getItem(key);
        }

        messagesHelper.send(source.getSender(), Message.builder("command.grant-success")
                .prefixed()
                .replacement("player", target.getName() != null ? target.getName() : target.getUniqueId().toString())
                .replacement("amount", String.valueOf(amount))
                .replacement("item", item.getDisplayName())
                .build());
    }

    private void onGivePool(CommandSourceStack source, PlayerSelectorArgumentResolver target, LootPool pool) throws CommandSyntaxException {
		List<Player> players = target.resolve(source);

        if (players.isEmpty()) {
            source.getSender()
                .sendMessage(Component.translatable("argument.entity.notfound.player")
                    .color(NamedTextColor.RED));

            return;
        }

        for(Player player: players) {
            pool.giveContents(player);
            messagesHelper.send(source.getSender(), Message.builder("command.give-pool-success")
                    .prefixed()
                    .replacement("player", player.getName())
                    .replacement("pool", pool.getName())
                    .build());
        }
	}

	private void onGivePlugin(CommandSourceStack source, PlayerSelectorArgumentResolver target, Plugin thePlugin) throws CommandSyntaxException {
		List<Player> players = target.resolve(source);

        if (players.isEmpty()) {
            source.getSender()
                .sendMessage(Component.translatable("argument.entity.notfound.player")
                    .color(NamedTextColor.RED));

            return;
        }

        for(Player player: players) {
			plugin.getItemManager().givePluginItems(player, thePlugin);
            messagesHelper.send(source.getSender(), Message.builder("command.give-plugin-success")
                    .prefixed()
                    .replacement("player", player.getName())
                    .replacement("plugin", thePlugin.getName())
                    .build());
        }
	}

    // customitems:revokeitem <player> <item>
    private void onRevokeItem(CommandSourceStack source, Object target, NamespacedKey key) throws CommandSyntaxException {
        if(target instanceof OfflinePlayer offlinePlayer) {
             onRevokeItem(source, offlinePlayer, key);
        } else if(target instanceof PlayerSelectorArgumentResolver selector) {
            List<Player> players = selector.resolve(source);

            if (players.isEmpty()) {
                source.getSender()
                        .sendMessage(Component.translatable("argument.entity.notfound.player")
                                             .color(NamedTextColor.RED));

                return;
            }

            for (Player player : players) {
                onRevokeItem(source, player, key);
            }
        } else {
            throw new IllegalArgumentException("target must be an OfflinePlayer or PlayerSelectorArgumentResolver");
        }
    }

    // customitems:revokeitem <offline-player> <item>
    private void onRevokeItem(CommandSourceStack source, OfflinePlayer target, NamespacedKey key) {
        int amount = plugin.getItemManager().revokeItem(target, key);
        CustomItem item = Util.isVanillaItem(key) ? Util.getVanillaCustomItem(key) : plugin.getItemManager().getItem(key);

        if(amount > 0) {
            messagesHelper.send(source.getSender(), Message.builder("command.revoke-success")
                    .prefixed()
                    .replacement("player", target.getName() != null ? target.getName() : target.getUniqueId().toString())
                    .replacement("amount", String.valueOf(amount))
                    .replacement("item", item.getDisplayName())
                    .build());
        } else {
            messagesHelper.send(source.getSender(), Message.builder("command.revoke-no-unclaimed")
                    .prefixed()
                    .type(Message.MessageType.ERROR)
                    .replacement("player", target.getName() != null ? target.getName() : target.getUniqueId().toString())
                    .replacement("item", item.getDisplayName())
                    .build());
        }
    }

    // customitems:viewunclaimed <player>
    private void onViewUnclaimed(CommandSourceStack source, PlayerSelectorArgumentResolver target) throws CommandSyntaxException {
        onViewUnclaimed(source, target.resolve(source).getFirst());
    }

    // customitems:viewunclaimed <offline-player>
    private void onViewUnclaimed(CommandSourceStack source, OfflinePlayer target) {
        if(!plugin.getItemManager().hasUnclaimedItems(target)) {
            messagesHelper.send(source.getSender(), Message.builder("command.no-unclaimed-items")
                    .prefixed()
                    .type(Message.MessageType.ERROR)
                    .replacement("player", target.getName() != null ? target.getName() : target.getUniqueId().toString())
                    .build());
            return;
        }

        plugin.getChestManager().showCommandClaimGUI((Player) source.getSender(), target);
    }

    private void onReload(CommandSourceStack source) {
        try {
		    plugin.initConfig();
            messagesHelper.send(source.getSender(), Message.builder("command.reload-success").prefixed().build());
        } catch(Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Error while reloading config: " + ex.getMessage(), ex);
            messagesHelper.send(source.getSender(), Message.builder("command.reload-error")
                    .prefixed()
                    .type(Message.MessageType.ERROR)
                    .build());
        }
	}
}
