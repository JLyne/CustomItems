package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class CustomItemBuilder {
	public static IDStep builder() {
		return new Steps();
	}

	public interface IDStep {
		DisplayNameStep id(NamespacedKey id);
	}

	public interface DisplayNameStep {
		GeneratorStep displayName(Component displayName);
	}

	public interface GeneratorStep {
		BuildStep generator(BiFunction<CreationContext, Integer, ItemStack> generator);
	}

	public interface BuildStep {
		@Deprecated(forRemoval = true)
		default BuildStep wearable() {
			return this;
		}
		@Deprecated(forRemoval = true)
		default BuildStep wearable(boolean wearable) {
			return this;
		}
		BuildStep stamp();
		BuildStep stamp(boolean stamp);
		CustomItem build();
	}

	private static class Steps implements IDStep, DisplayNameStep, GeneratorStep, BuildStep {
		private NamespacedKey id;
		private Component displayName;
		private BiFunction<CreationContext, Integer, ItemStack> generator;
		private boolean stamp = false;

		public CustomItem build() {
			return new ExternalCustomItem(id, displayName, generator, stamp);
		}

		public DisplayNameStep id(NamespacedKey id) {
			this.id = id;
			return this;
		}

		public GeneratorStep displayName(Component displayName) {
			this.displayName = displayName;
			return this;
		}

		public BuildStep generator(BiFunction<CreationContext, Integer, ItemStack> generator) {
			this.generator = generator;
			return this;
		}

		public BuildStep stamp() {
			this.stamp = true;
			return this;
		}

		public BuildStep stamp(boolean stamp) {
			this.stamp = stamp;
			return this;
		}
	}
}
