package uk.co.notnull.CustomItems;

import java.util.HashMap;
import java.util.Map;

public enum LootTier {
	INVALID(0),
	COMMON(1),
	UNCOMMON(2),
	RARE(3);

	private final int value;
    private static final Map<Integer, LootTier> map = new HashMap<>();

    LootTier(int value) {
        this.value = value;
    }

    static {
        for (LootTier tier : LootTier.values()) {
            map.put(tier.value, tier);
        }
    }

    public static LootTier valueOf(int tier) {
        LootTier match = map.get(tier);

        return match != null ? match : LootTier.INVALID;
    }

    public int getValue() {
        return value;
    }
}
