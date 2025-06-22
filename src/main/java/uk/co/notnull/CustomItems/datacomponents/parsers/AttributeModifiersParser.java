package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
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

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AttributeModifiersParser extends ListParser<ConfigurationSection, ItemAttributeModifiers> {
	private static final EquippableParser.EquipmentSlotGroupParser slotGroupParser =
			new EquippableParser.EquipmentSlotGroupParser();
	private static final EnumParser<AttributeModifier.Operation> operationParser =
			new EnumParser<>(AttributeModifier.Operation.class);

	private static final RegistryLookupParser<Attribute> attributeParser =
			new RegistryLookupParser<>(RegistryKey.ATTRIBUTE);

	@Override
	protected @NotNull Class<ConfigurationSection> getListItemType() {
		return ConfigurationSection.class;
	}

	protected ItemAttributeModifiers doParse(List<ConfigurationSection> value) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.itemAttributes();

		for (ConfigurationSection attribute: value) {
			Attribute type = requiredField("type", attributeParser, attribute);
			EquipmentSlotGroup slot = optionalField("slot", slotGroupParser, attribute, EquipmentSlotGroup.ANY);
			NamespacedKey id = requiredField("id", DataComponentParsers.NAMESPACED_KEY, attribute);
			double amount = requiredField("amount", DataComponentParsers.DOUBLE, attribute);
			AttributeModifier.Operation operation = requiredField("operation", operationParser, attribute);

			String displayField = optionalField("display", DataComponentParsers.STRING, attribute, "default");
			AttributeModifierDisplay display;

			switch (displayField) {
				case "default" -> display = AttributeModifierDisplay.reset();
				case "hidden" -> display = AttributeModifierDisplay.hidden();
				default -> display = AttributeModifierDisplay.override(DataComponentParsers.COMPONENT.parse(displayField));
			}

			builder.addModifier(type, new AttributeModifier(id, amount, operation, slot), display);
		}

		return builder.build();
	}
}
