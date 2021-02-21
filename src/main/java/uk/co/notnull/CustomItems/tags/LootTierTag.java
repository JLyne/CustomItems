package uk.co.notnull.CustomItems.tags;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import uk.co.notnull.CustomItems.LootTier;

public class LootTierTag implements PersistentDataType<Integer, LootTier> {
    @NotNull
    @Override
    public Class<Integer> getPrimitiveType() {
        return Integer.class;
    }

    @NotNull
    @Override
    public Class<LootTier> getComplexType() {
        return LootTier.class;
    }

    @NotNull
    public Integer toPrimitive(@NotNull LootTier complex, @NotNull PersistentDataAdapterContext context) {
        return complex.getValue();
    }

    @NotNull
    public LootTier fromPrimitive(@NotNull Integer primitive, @NotNull PersistentDataAdapterContext context) {
        return LootTier.valueOf(primitive);
    }
}
