package uk.co.notnull.CustomItems.datacomponents.parsers;

import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.datacomponents.DataComponentParsers;

@SuppressWarnings("UnstableApiUsage")
public class LodestoneTrackerParser extends DataComponentTypeParser<ConfigurationSection, LodestoneTracker> {
	@Override
	protected @NotNull Class<ConfigurationSection> getConfigType() {
		return ConfigurationSection.class;
	}

	protected LodestoneTracker doParse(ConfigurationSection value) {
		Boolean tracked = optionalField("tracked", DataComponentParsers.BOOLEAN, value);

		LodestoneTracker.Builder builder = LodestoneTracker.lodestoneTracker();

		if(value.contains("target")) {
			World world = Bukkit.getWorld(
					requiredField("target.dimension", DataComponentParsers.NAMESPACED_KEY, value));
			int x = requiredField("target.pos.x", DataComponentParsers.INT, value);
			int y = requiredField("target.pos.y", DataComponentParsers.INT, value);
			int z = requiredField("target.pos.z", DataComponentParsers.INT, value);

			builder.location(new Location(world, x, y, z));
		}

		if(tracked != null) {
			builder.tracked(tracked);
		}

		return builder.build();
	}
}
