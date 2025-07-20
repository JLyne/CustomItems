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

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.ItemManagerImpl;
import uk.co.notnull.CustomItems.api.items.CustomItem;
import uk.co.notnull.messageshelper.Message;

/**
 * Argument parser for {@link CustomItem CustomItems}
 *
 * @since 1.1.0
 */
public final class CustomItemArgumentType implements CustomArgumentType.Converted<NamespacedKey, NamespacedKey> {
	private final CustomItemsImpl plugin;
	private final ItemManagerImpl itemManager;

	public CustomItemArgumentType(CustomItemsImpl plugin) {
		this.plugin = plugin;
		this.itemManager = plugin.getItemManager();
	}

	@Override
	public @NotNull NamespacedKey convert(@NotNull NamespacedKey key) throws CommandSyntaxException {
		if(!itemManager.isValidId(key)) {
			Message message = Message.builder("command.invalid-item")
					.replacement("<input>", String.valueOf(key))
					.build();
			String messageString = plugin.getMessagesHelper().getString(message);

			throw new SimpleCommandExceptionType(new LiteralMessage(messageString)).create();
		}

        return key;
	}

	@Override
	public @NotNull ArgumentType<NamespacedKey> getNativeType() {
		return ArgumentTypes.namespacedKey();
	}

	@Override
	public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(
			com.mojang.brigadier.context.@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
		String search = builder.getRemainingLowerCase();

        itemManager.getItemIds().stream()
				.filter(id -> id.toString().startsWith(search) || id.getKey().startsWith(search))
				.map(NamespacedKey::toString)
				.forEach(builder::suggest);

		return CompletableFuture.completedFuture(builder.build());
	}
}
