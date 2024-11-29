package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.WrittenBookContent;
import io.papermc.paper.text.Filtered;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

import java.util.Collections;

@SuppressWarnings("UnstableApiUsage")
public class WrittenBookContentParser extends DataComponentTypeParser<ConfigurationSection, WrittenBookContent> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected WrittenBookContent doParse(ConfigurationSection value) {
		WrittenBookContent.Builder builder;

		String author = requiredField("author", DataComponentParsers.STRING, value);
		Integer generation = optionalField("generation", DataComponentParsers.INT, value);
		Boolean resolved = optionalField("resolved", DataComponentParsers.BOOLEAN, value);

		if(value.isConfigurationSection("title")) {
			String raw = requiredField("title.raw", DataComponentParsers.STRING, value);
			String filtered = optionalField("title.filtered", DataComponentParsers.STRING, value);

			Filtered<String> filteredTitle = Filtered.of(raw, filtered);
			builder = WrittenBookContent.writtenBookContent(filteredTitle, author);
		} else {
			String title = requiredField("title", DataComponentParsers.STRING, value);
			builder = WrittenBookContent.writtenBookContent(title, author);
		}

		if(generation != null) {
			builder.generation(generation);
		}

		if(resolved != null) {
			builder.resolved(resolved);
		}

		for (Object page : value.getList("writable_book_content", Collections.emptyList())) {
			if(page instanceof ConfigurationSection section) {
				Component raw = requiredField("raw", DataComponentParsers.COMPONENT, section);
				Component filtered = optionalField("filtered", DataComponentParsers.COMPONENT, section);

				builder.addFilteredPage(Filtered.of(raw, filtered));
			} else {
				builder.addPage(DataComponentParsers.COMPONENT.parse(page));
			}
		}

		return builder.build();
	}
}
