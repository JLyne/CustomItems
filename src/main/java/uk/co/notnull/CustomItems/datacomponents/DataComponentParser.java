package uk.co.notnull.CustomItems.datacomponents;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import uk.co.notnull.CustomItems.CustomItemsImpl;
import uk.co.notnull.CustomItems.datacomponents.parsers.DataComponentTypeParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class DataComponentParser {
	private static final Map<DataComponentType, DataComponentTypeParser<?, ?>> parsers = new HashMap<>();

	static {
		parsers.put(DataComponentTypes.ATTACK_RANGE, DataComponentParsers.ATTACK_RANGE);
		parsers.put(DataComponentTypes.ATTRIBUTE_MODIFIERS, DataComponentParsers.ATTRIBUTE_MODIFIERS);
		parsers.put(DataComponentTypes.BANNER_PATTERNS, DataComponentParsers.BANNER_PATTERNS);
		parsers.put(DataComponentTypes.BASE_COLOR, DataComponentParsers.BASE_COLOR);
		//TODO: BEES - Not implemented by paper
		//TODO: BLOCK_ENTITY_DATA - Not implemented by paper
		//TODO: BLOCK_STATE - Builder not implemented by paper
		parsers.put(DataComponentTypes.BLOCKS_ATTACKS, DataComponentParsers.BLOCKS_ATTACKS);
		parsers.put(DataComponentTypes.BREAK_SOUND, DataComponentParsers.NAMESPACED_KEY);
		//TODO: BUCKET_ENTITY_DATA - Not implemented by paper
		//TODO: BUNDLE_CONTENTS - Need ItemStack parser
		parsers.put(DataComponentTypes.CAN_PLACE_ON, DataComponentParsers.ADVENTURE_PREDICATE);
		parsers.put(DataComponentTypes.CAN_BREAK, DataComponentParsers.ADVENTURE_PREDICATE);
		//TODO: CHARGED_PROJECTILES - Need ItemStack parser
		parsers.put(DataComponentTypes.CONSUMABLE, DataComponentParsers.CONSUMABLE);
		//TODO: CONTAINER - Need ItemStack parser
		parsers.put(DataComponentTypes.CONTAINER_LOOT, DataComponentParsers.CONTAINER_LOOT);
		//TODO: CUSTOM_DATA - Can't use directly, only with PersistentDataContainer
		parsers.put(DataComponentTypes.CUSTOM_MODEL_DATA, DataComponentParsers.CUSTOM_MODEL_DATA);
		parsers.put(DataComponentTypes.CUSTOM_NAME, DataComponentParsers.COMPONENT);
		parsers.put(DataComponentTypes.DAMAGE, DataComponentParsers.INT);
		parsers.put(DataComponentTypes.DAMAGE_TYPE, DataComponentParsers.DAMAGE_TYPE);
		parsers.put(DataComponentTypes.DAMAGE_RESISTANT, DataComponentParsers.DAMAGE_RESISTANT);
		//TODO: DEBUG_STICK_STATE - Not implemented by paper
		parsers.put(DataComponentTypes.DEATH_PROTECTION, DataComponentParsers.DEATH_PROTECTION);
		parsers.put(DataComponentTypes.DYED_COLOR, DataComponentParsers.DYED_COLOR);
		parsers.put(DataComponentTypes.ENCHANTABLE, DataComponentParsers.ENCHANTABLE);
		parsers.put(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, DataComponentParsers.BOOLEAN);
		parsers.put(DataComponentTypes.ENCHANTMENTS, DataComponentParsers.ENCHANTMENTS);
		//TODO: ENTITY_DATA - Not implemented by paper
		parsers.put(DataComponentTypes.EQUIPPABLE, DataComponentParsers.EQUIPPABLE);
		parsers.put(DataComponentTypes.FIREWORK_EXPLOSION, DataComponentParsers.FIREWORK_EXPLOSION);
		parsers.put(DataComponentTypes.FIREWORKS, DataComponentParsers.FIREWORKS);
		parsers.put(DataComponentTypes.FOOD, DataComponentParsers.FOOD);
		// GLIDER - Unvalued
		parsers.put(DataComponentTypes.INSTRUMENT, DataComponentParsers.INSTRUMENT);
		// INTANGIBLE_PROJECTILE - Unvalued
		parsers.put(DataComponentTypes.ITEM_MODEL, DataComponentParsers.NAMESPACED_KEY);
		parsers.put(DataComponentTypes.ITEM_NAME, DataComponentParsers.COMPONENT);
		parsers.put(DataComponentTypes.JUKEBOX_PLAYABLE, DataComponentParsers.JUKEBOX_PLAYABLE);
		parsers.put(DataComponentTypes.KINETIC_WEAPON, DataComponentParsers.KINETIC_WEAPON);
		//TODO: LOCK - Not implemented by paper
		parsers.put(DataComponentTypes.LODESTONE_TRACKER, DataComponentParsers.LODESTONE_TRACKER);
		parsers.put(DataComponentTypes.LORE, DataComponentParsers.LORE);
		parsers.put(DataComponentTypes.MAP_COLOR, DataComponentParsers.MAP_COLOR);
		parsers.put(DataComponentTypes.MAP_DECORATIONS, DataComponentParsers.MAP_DECORATIONS);
		parsers.put(DataComponentTypes.MAP_ID, DataComponentParsers.MAP_ID);
		parsers.put(DataComponentTypes.MAP_POST_PROCESSING, DataComponentParsers.MAP_POST_PROCESSING);
		parsers.put(DataComponentTypes.MAX_DAMAGE, DataComponentParsers.INT);
		parsers.put(DataComponentTypes.MAX_STACK_SIZE, DataComponentParsers.INT);
		parsers.put(DataComponentTypes.MINIMUM_ATTACK_CHARGE, DataComponentParsers.FLOAT);
		parsers.put(DataComponentTypes.NOTE_BLOCK_SOUND, DataComponentParsers.NAMESPACED_KEY);
		parsers.put(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, DataComponentParsers.OMINOUS_BOTTLE_AMPLIFIER);
		parsers.put(DataComponentTypes.PIERCING_WEAPON, DataComponentParsers.PIERCING_WEAPON);
		parsers.put(DataComponentTypes.POT_DECORATIONS, DataComponentParsers.POT_DECORATIONS);
		parsers.put(DataComponentTypes.POTION_CONTENTS, DataComponentParsers.POTION_CONTENTS);
		parsers.put(DataComponentTypes.POTION_DURATION_SCALE, DataComponentParsers.FLOAT);
		parsers.put(DataComponentTypes.PROFILE, DataComponentParsers.PROFILE);
		parsers.put(DataComponentTypes.PROVIDES_BANNER_PATTERNS, DataComponentParsers.PROVIDES_BANNER_PATTERNS);
		parsers.put(DataComponentTypes.PROVIDES_TRIM_MATERIAL, DataComponentParsers.PROVIDES_TRIM_MATERIAL);
		parsers.put(DataComponentTypes.RARITY, DataComponentParsers.RARITY);
		//parsers.put(DataComponentTypes.RECIPES, DataComponentParsers.NAMESPACED_KEY_LIST); //FIXME
		parsers.put(DataComponentTypes.REPAIRABLE, DataComponentParsers.REPAIRABLE);
		parsers.put(DataComponentTypes.REPAIR_COST, DataComponentParsers.INT);
		parsers.put(DataComponentTypes.STORED_ENCHANTMENTS, DataComponentParsers.ENCHANTMENTS);
		parsers.put(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, DataComponentParsers.SUSPICIOUS_STEW_CONTENTS);
		parsers.put(DataComponentTypes.SWING_ANIMATION, DataComponentParsers.SWING_ANIMATION);
		parsers.put(DataComponentTypes.TOOL, DataComponentParsers.TOOL);
		parsers.put(DataComponentTypes.TOOLTIP_DISPLAY, DataComponentParsers.TOOLTIP_DISPLAY);
		parsers.put(DataComponentTypes.TOOLTIP_STYLE, DataComponentParsers.NAMESPACED_KEY);
		parsers.put(DataComponentTypes.TRIM, DataComponentParsers.TRIM);
		// UNBREAKABLE - Unvalued
		parsers.put(DataComponentTypes.USE_COOLDOWN, DataComponentParsers.USE_COOLDOWN);
		parsers.put(DataComponentTypes.USE_EFFECTS, DataComponentParsers.USE_EFFECTS);
		//TODO: USE_REMAINDER - Need ItemStack parser
		parsers.put(DataComponentTypes.WEAPON, DataComponentParsers.WEAPON);
		parsers.put(DataComponentTypes.WRITABLE_BOOK_CONTENT, DataComponentParsers.WRITABLE_BOOK_CONTENT);
		parsers.put(DataComponentTypes.WRITTEN_BOOK_CONTENT, DataComponentParsers.WRITTEN_BOOK_CONTENT);
	}

	public static Map<DataComponentType, Object> parse(ConfigurationSection componentConfig, String name) {
		Registry<@org.jetbrains.annotations.NotNull DataComponentType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE);
		Map<DataComponentType, Object> result = new HashMap<>();

		if(componentConfig == null) {
			return Collections.emptyMap();
		}

		for (String key : componentConfig.getKeys(false)) {
			DataComponentType type = registry.get(NamespacedKey.fromString(key));

			if(type == null) {
				CustomItemsImpl.getInstance().getLogger().warning("Item " + name + ": Ignoring unknown component type " + key); //TODO: Item name
				continue;
			}

			if(type instanceof DataComponentType.NonValued) {
				result.put(type, componentConfig.getBoolean(key, true));
				continue;
			}

			if(!parsers.containsKey(type)) {
				CustomItemsImpl.getInstance().getLogger().warning("Item " + name + ": Ignoring unsupported component type " + key); //TODO: Item name
				continue;
			}

			try {
				if(type instanceof DataComponentType.Valued) {
					result.put(type, parsers.get(type).parse(componentConfig.get(key)));
				}
			} catch(Exception e) {
				CustomItemsImpl.getInstance().getLogger().warning("Item " + name + ": Error parsing " + key + ": " + e.getMessage());
			}
		}

		return result;
	}
}
