package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;


@SuppressWarnings("UnstableApiUsage")
public class BlocksAttacksParser extends DataComponentTypeParser<ConfigurationSection, BlocksAttacks> {
	private static final DamageReductionParser damageReductionParser = new DamageReductionParser();
	private static final ItemDamageFunctionParser itemDamageFunctionParser = new ItemDamageFunctionParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected BlocksAttacks doParse(ConfigurationSection value) {
		Float blockDelaySeconds = optionalField("block_delay_seconds", DataComponentParsers.FLOAT, value);
		Float disableCooldownScale = optionalField("disable_cooldown_scale", DataComponentParsers.FLOAT, value);

		ItemDamageFunction itemDamageFunction = optionalField("item_damage", itemDamageFunctionParser, value);
		List<DamageReduction> damageReductions = optionalListField("damage_reductions", damageReductionParser, value);

		NamespacedKey blockSound = optionalField("block_sound", DataComponentParsers.NAMESPACED_KEY, value);
		NamespacedKey disabledSound = optionalField("disabled_sound", DataComponentParsers.NAMESPACED_KEY, value);
		NamespacedKey bypassedBy = optionalField("bypassed_by", DataComponentParsers.NAMESPACED_KEY, value);

		BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks();

		if(blockDelaySeconds != null) {
			builder.blockDelaySeconds(blockDelaySeconds);
		}

		if(disableCooldownScale != null) {
			builder.disableCooldownScale(disableCooldownScale);
		}

		if(damageReductions != null) {
			builder.damageReductions(damageReductions);
		}

		if(itemDamageFunction != null) {
			builder.itemDamage(itemDamageFunction);
		}

		if(blockSound != null) {
			builder.blockSound(blockSound);
		}

		if(disabledSound != null) {
			builder.disableSound(disabledSound);
		}

		if(bypassedBy != null) {
			builder.bypassedBy(TagKey.create(RegistryKey.DAMAGE_TYPE, bypassedBy));
		}

		return builder.build();
	}

	private static class DamageReductionParser extends DataComponentTypeParser<ConfigurationSection, DamageReduction> {
		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected DamageReduction doParse(ConfigurationSection value) {
			Float base = optionalField("base", DataComponentParsers.FLOAT, value);
			Float factor = optionalField("factor", DataComponentParsers.FLOAT, value);
			Float horizontalBlockingAngle = optionalField("horizontal_blocking_angle", DataComponentParsers.FLOAT, value);

			DamageReduction.Builder damageReduction = DamageReduction.damageReduction();

			if(base != null) {
				damageReduction.base(base);
			}

			if(factor != null) {
				damageReduction.factor(factor);
			}

			if(horizontalBlockingAngle != null) {
				damageReduction.horizontalBlockingAngle(horizontalBlockingAngle);
			}

			return damageReduction.build();
		}
	}

	private static class ItemDamageFunctionParser extends DataComponentTypeParser<ConfigurationSection, ItemDamageFunction> {
		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected ItemDamageFunction doParse(ConfigurationSection value) {
			Float threshold = optionalField("threshold", DataComponentParsers.FLOAT, value);
			Float base = optionalField("base", DataComponentParsers.FLOAT, value);
			Float factor = optionalField("factor", DataComponentParsers.FLOAT, value);

			ItemDamageFunction.Builder itemDamageFunction = ItemDamageFunction.itemDamageFunction();

			if(threshold != null) {
				itemDamageFunction.threshold(threshold);
			}

			if(base != null) {
				itemDamageFunction.base(base);
			}

			if(factor != null) {
				itemDamageFunction.factor(factor);
			}

			return itemDamageFunction.build();
		}
	}
}
