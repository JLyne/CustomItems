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

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.NonNull;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.api.ItemManager;
import uk.co.notnull.CustomItems.api.items.CustomItem;

/**
 * Argument parser for {@link CustomItem CustomItems}
 *
 * @param <C> Command sender type
 * @since 1.1.0
 */
public final class CustomItemParser<C> implements ArgumentParser<C, CustomItem> {

	public static final Caption ARGUMENT_PARSE_FAILURE_CUSTOM_ITEM =
			Caption.of("argument.parse.failure.custom_item");

	private final CustomItemsImpl plugin;
	private final ItemManager itemManager;

	public CustomItemParser(CustomItemsImpl plugin) {
		this.plugin = plugin;
		this.itemManager = plugin.getItemManager();
	}

    @Override
    public @NonNull ArgumentParseResult<@NonNull CustomItem> parse(
            final @NonNull CommandContext<@NonNull C> commandContext,
            final @NonNull Queue<@NonNull String> inputQueue
    ) {
        final String input = inputQueue.peek();

        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(
                    CustomItemParser.class,
                    commandContext
            ));
        }

		NamespacedKey id = NamespacedKey.fromString(input, plugin);

		if(!itemManager.isValidId(id)) {
			return ArgumentParseResult.failure(new CustomItemParseException(input, commandContext));
		}

        inputQueue.remove();
        return ArgumentParseResult.success(itemManager.getItem(id));
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(
            final @NonNull CommandContext<C> commandContext,
            final @NonNull String input
    ) {
        return itemManager.getItemIds().stream()
				.filter(id -> id.getKey().startsWith(input) || id.getNamespace().startsWith(input))
				.map(NamespacedKey::toString)
				.collect(Collectors.toList());
    }

    @Override
    public boolean isContextFree() {
        return true;
    }

    public static final class CustomItemParseException extends ParserException {
        private CustomItemParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    CustomItemParser.class,
                    context,
                    ARGUMENT_PARSE_FAILURE_CUSTOM_ITEM,
                    CaptionVariable.of("input", input)
            );
        }
    }
}
