//
// MIT License
//
// Copyright (c) 2021 Alexander Söderberg & Contributors
// Copyright (c) 2022 James Lyne
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package uk.co.notnull.CustomItems.commands;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.messageshelper.Message;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Argument parser for {@link OfflinePlayer OfflinePlayers}
 *
 * @since 1.1.0
 */
@SuppressWarnings("UnstableApiUsage")
public final class OfflinePlayerArgumentType implements CustomArgumentType.Converted<OfflinePlayer, String> {
	private final CustomItemsImpl plugin;

	public OfflinePlayerArgumentType(CustomItemsImpl plugin) {
		this.plugin = plugin;
	}

	@Override
	public @NotNull OfflinePlayer convert(@NotNull String id) throws CommandSyntaxException {
		OfflinePlayer result = Bukkit.getOfflinePlayerIfCached(id);

		if(result == null) {
			try {
				result = Bukkit.getOfflinePlayer(UUID.fromString(id));
			} catch (IllegalArgumentException e) {
				Message message = Message.builder("command.invalid-player")
					.replacement("<input>", id)
					.build();
				String messageString = plugin.getMessagesHelper().getString(message);

				throw new SimpleCommandExceptionType(new LiteralMessage(messageString)).create();
			}
		}

		return result;
	}

	@Override
	public @NotNull ArgumentType<String> getNativeType() {
		return StringArgumentType.word();
	}

	@Override
	public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(
			com.mojang.brigadier.context.@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		String search = builder.getRemainingLowerCase();

		if(context.getSource() instanceof CommandSourceStack stack) {
			CommandSender sender = stack.getSender();

			Bukkit.getOnlinePlayers().stream()
					.filter(player -> !(sender instanceof Player p && !p.canSee(player)))
					.map(Player::getName)
					.filter(name -> name.startsWith(search))
					.forEach(builder::suggest);
		}

		return CompletableFuture.completedFuture(builder.build());
	}
}
