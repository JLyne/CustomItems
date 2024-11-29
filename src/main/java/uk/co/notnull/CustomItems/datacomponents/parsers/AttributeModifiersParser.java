package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.EnumParser;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

@SuppressWarnings("UnstableApiUsage")
public class AttributeModifiersParser extends DataComponentTypeParser<ConfigurationSection, ItemAttributeModifiers> {
	private static final EquippableParser.EquipmentSlotGroupParser slotGroupParser =
			new EquippableParser.EquipmentSlotGroupParser();
	private static final EnumParser<AttributeModifier.Operation> operationParser =
			new EnumParser<>(AttributeModifier.Operation.class);

	private static final RegistryLookupParser<Attribute> attributeParser =
			new RegistryLookupParser<>(RegistryKey.ATTRIBUTE);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected ItemAttributeModifiers doParse(ConfigurationSection value) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

		for (String key : value.getKeys(false)) {
			EquipmentSlotGroup slot = optionalField(key + ".slot", slotGroupParser, value, EquipmentSlotGroup.ANY);
			NamespacedKey id = requiredField(key + ".id", DataComponentParsers.NAMESPACED_KEY, value);
			double amount = requiredField(key + ".amount", DataComponentParsers.DOUBLE, value);
			AttributeModifier.Operation operation = requiredField(key + ".operation", operationParser, value);

			builder.addModifier(attributeParser.parse(key), new AttributeModifier(id, amount, operation, slot));
		}

		builder.showInTooltip(DataComponentParsers.SHOW_IN_TOOLTIP.parse(value));

		return builder.build();
	}
}
