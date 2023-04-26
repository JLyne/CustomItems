package uk.co.notnull.CustomItems.api.items;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class CustomItemBuilder {
	public static IDStep builder() {
		return new Steps();
	}

	public interface IDStep {
		DisplayNameStep id(String id);
	}

	public interface DisplayNameStep {
		GeneratorStep displayName(Component displayName);
	}

	public interface GeneratorStep {
		BuildStep generator(BiFunction<CreationContext, Integer, ItemStack> generator);
	}

	public interface BuildStep {
		BuildStep wearable();
		BuildStep wearable(boolean wearable);
		BuildStep stamp();
		BuildStep stamp(boolean wearable);
		CustomItem build();
	}

	private static class Steps implements IDStep, DisplayNameStep, GeneratorStep, BuildStep {
		private String id;
		private Component displayName;
		private BiFunction<CreationContext, Integer, ItemStack> generator;
		private boolean wearable = false;
		private boolean stamp = false;

		public CustomItem build() {
			return new ExternalCustomItem(id, displayName, generator, wearable, stamp);
		}

		public DisplayNameStep id(String id) {
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

		public BuildStep wearable() {
			this.wearable = true;
			return this;
		}

		public BuildStep wearable(boolean wearable) {
			this.wearable = wearable;
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
