package uk.co.notnull.CustomItems.api;

import uk.co.notnull.CustomItems.api.loot.LootManager;

@SuppressWarnings("unused")
public interface CustomItems {
	ItemManager getItemManager();
	LootManager getLootManager();
}