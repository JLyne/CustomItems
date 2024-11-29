package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.WritableBookContent;
import io.papermc.paper.text.Filtered;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class WritableBookContentParser extends ListParser<Object, WritableBookContent> {
	@Override
	protected @NotNull Class<Object> getListItemType() {
		return Object.class;
	}

	protected WritableBookContent doParse(List<Object> value) {
		WritableBookContent.Builder builder = WritableBookContent.writeableBookContent();

		for (Object page : value) {
			if(page instanceof ConfigurationSection section) {
				String raw = requiredField("raw", DataComponentParsers.STRING, section);
				String filtered = optionalField("filtered", DataComponentParsers.STRING, section);

				builder.addFilteredPage(Filtered.of(raw, filtered));
			} else {
				builder.addPage(DataComponentParsers.STRING.parse(page));
			}
		}

		return builder.build();
	}
}
