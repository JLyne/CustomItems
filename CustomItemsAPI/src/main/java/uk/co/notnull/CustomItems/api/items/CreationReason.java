package uk.co.notnull.CustomItems.api.items;

public enum CreationReason {
	/**
	 * Item is being added directory to the player's inventory
	 */
	GIVEN,
	/**
	 * Item was previously granted and is being displayed in the Unclaimed Items GUI
	 */
	GRANTED,
	/**
	 * Item is being generated as part of a loot table
	 */
	LOOT,
}
