package uk.co.notnull.CustomItems.creativeitemfilter;

import org.bukkit.Bukkit;
import org.hurricanegames.creativeitemfilter.CreativeItemFilter;
import org.hurricanegames.creativeitemfilter.handler.component.ItemComponentPopulatorFactory;
import uk.co.notnull.CustomItems.CustomItemsImpl;

public class CreativeItemFilterHandler {

	public CreativeItemFilterHandler(CustomItemsImpl plugin) {
		boolean cifEnabled = Bukkit.getPluginManager().isPluginEnabled("CreativeItemFilter");

		if(!cifEnabled) {
			return;
		}

		ItemComponentPopulatorFactory factory = ((CreativeItemFilter) Bukkit.getPluginManager().getPlugin("CreativeItemFilter"))
				.getComponentPopulatorFactory();

		factory.addPopulator(new CustomItemComponentPopulator(plugin));
	}
}
