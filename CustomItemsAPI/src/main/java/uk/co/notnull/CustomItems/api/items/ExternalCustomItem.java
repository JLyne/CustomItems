package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

final class ExternalCustomItem extends AbstractCustomItem {
	private final BiFunction<CreationContext, Integer, ItemStack> generator;

	public ExternalCustomItem(String id, Component displayName, BiFunction<CreationContext, Integer, ItemStack> generator, boolean wearable, boolean stamp) {
		super(id, displayName, wearable, stamp);
		this.generator = generator;
	}

	@Override
	public ItemStack createItem(CreationContext context, int amount) {
		return generator.apply(context, amount);
	}
}

