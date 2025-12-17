package uk.co.notnull.CustomItems.datacomponents;

import io.papermc.paper.item.MapPostProcessing;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.MusicInstrument;
import org.bukkit.block.banner.PatternType;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import uk.co.notnull.CustomItems.datacomponents.parsers.*;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.*;

public class DataComponentParsers {
	public static final IntParser INT = new IntParser();
	public static final DoubleParser DOUBLE = new DoubleParser();
	public static final FloatParser FLOAT = new FloatParser();
	public static final LongParser LONG = new LongParser();
	public static final BooleanParser BOOLEAN = new BooleanParser();
	public static final StringParser STRING = new StringParser();

	public static final UUIDParser UUID = new UUIDParser();
	public static final ComponentParser COMPONENT = new ComponentParser();
	public static final NamespacedKeyParser NAMESPACED_KEY = new NamespacedKeyParser();
	public static final ColorParser COLOR = new ColorParser();

	public static final ConsumeEffectListParser CONSUME_EFFECT = new ConsumeEffectListParser();

	public static final LoreParser LORE = new LoreParser();
	public static final EnumParser<ItemRarity> RARITY = new EnumParser<>(ItemRarity.class);
	public static final EnchantmentsParser ENCHANTMENTS = new EnchantmentsParser();
	public static final AdventurePredicateParser ADVENTURE_PREDICATE = new AdventurePredicateParser();
	public static final AttributeModifiersParser ATTRIBUTE_MODIFIERS = new AttributeModifiersParser();
	public static final CustomModelDataParser CUSTOM_MODEL_DATA = new CustomModelDataParser();
	public static final FoodParser FOOD = new FoodParser();
	public static final ConsumableParser CONSUMABLE = new ConsumableParser();
	public static final UseCooldownParser USE_COOLDOWN = new UseCooldownParser();
	public static final DamageResistantParser DAMAGE_RESISTANT = new DamageResistantParser();
	public static final ToolParser TOOL = new ToolParser();
	public static final EnchantableParser ENCHANTABLE = new EnchantableParser();
	public static final EquippableParser EQUIPPABLE = new EquippableParser();
	public static final RepairableParser REPAIRABLE = new RepairableParser();
	public static final DeathProtectionParser DEATH_PROTECTION = new DeathProtectionParser();
	public static final DyedColorParser DYED_COLOR = new DyedColorParser();
	public static final MapIdParser MAP_ID = new MapIdParser();
	public static final MapColorParser MAP_COLOR = new MapColorParser();
	public static final MapDecorationsParser MAP_DECORATIONS = new MapDecorationsParser();
	public static final EnumParser<MapPostProcessing> MAP_POST_PROCESSING = new EnumParser<>(MapPostProcessing.class);
	public static final PotionContentsParser POTION_CONTENTS = new PotionContentsParser();
	public static final SuspiciousStewEffectsParser SUSPICIOUS_STEW_CONTENTS = new SuspiciousStewEffectsParser();
	public static final WritableBookContentParser WRITABLE_BOOK_CONTENT = new WritableBookContentParser();
	public static final WrittenBookContentParser WRITTEN_BOOK_CONTENT = new WrittenBookContentParser();
	public static final TrimParser TRIM = new TrimParser();
	public static final RegistryLookupParser<MusicInstrument> INSTRUMENT = new RegistryLookupParser<>(RegistryKey.INSTRUMENT);
	public static final OminousBottleAmplifierParser OMINOUS_BOTTLE_AMPLIFIER = new OminousBottleAmplifierParser();
	public static final JukeboxPlayableParser JUKEBOX_PLAYABLE = new JukeboxPlayableParser();
	public static final LodestoneTrackerParser LODESTONE_TRACKER = new LodestoneTrackerParser();
	public static final FireworkExplosionParser FIREWORK_EXPLOSION = new FireworkExplosionParser();
	public static final FireworksParser FIREWORKS = new FireworksParser();
	public static final ProfileParser PROFILE = new ProfileParser();
	public static final BannerPatternsParser BANNER_PATTERNS = new BannerPatternsParser();
	public static final EnumParser<DyeColor> BASE_COLOR = new EnumParser<>(DyeColor.class);
	public static final PotDecorationsParser POT_DECORATIONS = new PotDecorationsParser();
	public static final ContainerLootParser CONTAINER_LOOT = new ContainerLootParser();
	public static final BlocksAttacksParser BLOCKS_ATTACKS = new BlocksAttacksParser();
	public static final RegistryLookupParser<TrimMaterial> PROVIDES_TRIM_MATERIAL = new RegistryLookupParser<>(RegistryKey.TRIM_MATERIAL);
	public static final TagKeyParser<PatternType> PROVIDES_BANNER_PATTERNS = new TagKeyParser<>(RegistryKey.BANNER_PATTERN);
	public static final TooltipDisplayParser TOOLTIP_DISPLAY = new TooltipDisplayParser();
	public static final WeaponParser WEAPON = new WeaponParser();
	public static final AttackRangeParser ATTACK_RANGE = new AttackRangeParser();
	public static final RegistryLookupParser<DamageType> DAMAGE_TYPE = new RegistryLookupParser<>(RegistryKey.DAMAGE_TYPE);
	public static final KineticWeaponParser KINETIC_WEAPON = new KineticWeaponParser();
	public static final PiercingWeaponParser PIERCING_WEAPON = new PiercingWeaponParser();
	public static final SwingAnimationParser SWING_ANIMATION = new SwingAnimationParser();
	public static final UseEffectsParser USE_EFFECTS = new UseEffectsParser();
}
