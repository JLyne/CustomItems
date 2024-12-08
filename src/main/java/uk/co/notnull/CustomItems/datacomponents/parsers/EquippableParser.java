package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryKeySetParser;

@SuppressWarnings("UnstableApiUsage")
public class EquippableParser extends DataComponentTypeParser<ConfigurationSection, Equippable> {
	EquipmentSlotGroupParser slotGroupParser = new EquipmentSlotGroupParser();
	RegistryKeySetParser<EntityType> keySetParser = new RegistryKeySetParser<>(RegistryKey.ENTITY_TYPE, true);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected Equippable doParse(ConfigurationSection value) {
		EquipmentSlotGroup slotGroup = requiredField("slot", slotGroupParser, value);
		NamespacedKey equipSound = optionalField("equip_sound", DataComponentParsers.NAMESPACED_KEY, value);
		NamespacedKey model = optionalField("model", DataComponentParsers.NAMESPACED_KEY, value);
		Boolean dispensable = optionalField("dispensable", DataComponentParsers.BOOLEAN, value);
		Boolean swappable = optionalField("swappable", DataComponentParsers.BOOLEAN, value);
		Boolean damageOnHurt = optionalField("damage_on_hurt", DataComponentParsers.BOOLEAN, value);
		NamespacedKey cameraOverlay = optionalField("camera_overlay", DataComponentParsers.NAMESPACED_KEY, value);
		RegistryKeySet<EntityType> allowedEntities = optionalField("allowedEntities", keySetParser, value);

		Equippable.Builder builder = Equippable.equippable(slotGroup.getExample()); //FIXME: Hack since API seems to be using the wrong class atm?

		if(equipSound != null) {
			builder.equipSound(equipSound);
		}

		if(model != null) {
			builder.assetId(model);
		}

		if(dispensable != null) {
			builder.dispensable(dispensable);
		}

		if(swappable != null) {
			builder.swappable(swappable);
		}

		if(damageOnHurt != null) {
			builder.damageOnHurt(damageOnHurt);
		}

		if(cameraOverlay != null) {
			builder.cameraOverlay(cameraOverlay);
		}

		if(allowedEntities != null) {
			builder.allowedEntities(allowedEntities);
		}

		return builder.build();
	}

	public static class EquipmentSlotGroupParser extends DataComponentTypeParser<String, EquipmentSlotGroup> {
		@Override
		protected @NotNull Class<String> getConfigType() {
			return String.class;
		}

		protected EquipmentSlotGroup doParse(String value) {
			EquipmentSlotGroup group = EquipmentSlotGroup.getByName(value);

			if(group == null) {
				throw new IllegalArgumentException("Unknown slot: " + value);
			}

			return group;
		}
	}
}
