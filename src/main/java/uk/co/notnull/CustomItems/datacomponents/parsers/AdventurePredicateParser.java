package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;
import uk.co.notnull.CustomItems.datacomponents.parsers.simple.RegistryKeySetParser;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class AdventurePredicateParser extends DataComponentTypeParser<ConfigurationSection, ItemAdventurePredicate> {
	private static final RegistryKeySetParser<BlockType> keySetParser =
			new RegistryKeySetParser<>(RegistryKey.BLOCK, true);
	private static final PredicateParser predicateParser = new PredicateParser();

	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected ItemAdventurePredicate doParse(ConfigurationSection value) {
		List<BlockPredicate> predicateList = optionalListField("predicates", predicateParser, value);

		ItemAdventurePredicate.Builder builder = ItemAdventurePredicate.itemAdventurePredicate();

		if(predicateList != null) {
			builder.addPredicates(predicateList);
		} else {
			builder.addPredicate(predicateParser.parse(value));
		}

		builder.showInTooltip(DataComponentParsers.SHOW_IN_TOOLTIP.parse(value));

		return builder.build();
	}

	private static class PredicateParser extends DataComponentTypeParser<ConfigurationSection, BlockPredicate> {
		@Override
		protected @NotNull Class<ConfigurationSection> getConfigType() {
			return ConfigurationSection.class;
		}

		@Override
		protected BlockPredicate doParse(ConfigurationSection value) {
			RegistryKeySet<BlockType> blocks = requiredField("blocks", keySetParser, value);
			return BlockPredicate.predicate().blocks(blocks).build();
		}
	}
}
