package uk.co.notnull.CustomItems.api.items.provider;


import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.notnull.CustomItems.api.items.CustomItem;

import java.util.Collection;

public interface CustomItemProvider {
	@NotNull Plugin getPlugin();
	@NotNull Collection<CustomItem> provideItems();
	@Nullable CustomItem identifyItem(ItemStack item);
}
