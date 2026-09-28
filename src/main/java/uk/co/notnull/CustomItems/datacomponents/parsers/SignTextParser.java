package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.SignText;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.EnumParser;

import java.util.List;

public class SignTextParser extends DataComponentTypeParser<ConfigurationSection, SignText> {
	private static final EnumParser<DyeColor> dyeColorParser = new EnumParser<>(DyeColor.class);

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected SignText doParse(ConfigurationSection value) {
		SignText.Builder builder = SignText.signText();

		List<Component> messages = optionalListField("messages", DataComponentParsers.COMPONENT, value);
		DyeColor color = optionalField("color", dyeColorParser, value);
		Boolean hasGlowingText = optionalField("has_glowing_text", DataComponentParsers.BOOLEAN, value);

		if(messages != null) {
			builder.lines(messages);
		}
		
		if(color != null) {
			builder.color(color);
		}

		if(hasGlowingText != null) {
			builder.hasGlowingText(hasGlowingText);
		}

		return builder.build();
	}
}
