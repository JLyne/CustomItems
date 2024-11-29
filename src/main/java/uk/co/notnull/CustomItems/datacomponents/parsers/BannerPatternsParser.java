package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.EnumParser;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryLookupParser;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class BannerPatternsParser extends ListParser<ConfigurationSection, BannerPatternLayers> {
	private static final PatternParser patternLayerParser = new PatternParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getListItemType() {
		return ConfigurationSection.class;
	}

	protected BannerPatternLayers doParse(List<ConfigurationSection> value) {
		return BannerPatternLayers.bannerPatternLayers(patternLayerParser.parseList(value));
	}

	private static class PatternParser extends DataComponentTypeParser<ConfigurationSection, Pattern> {
		private static final EnumParser<DyeColor> colorParser = new EnumParser<>(DyeColor.class);
		private static final RegistryLookupParser<PatternType> patternParser =
				new RegistryLookupParser<>(RegistryKey.BANNER_PATTERN);

		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected Pattern doParse(ConfigurationSection value) {
			PatternType patternType = requiredField("pattern", patternParser, value);
			DyeColor color = requiredField("color", colorParser, value);

			return new Pattern(color, patternType);
		}
	}
}
