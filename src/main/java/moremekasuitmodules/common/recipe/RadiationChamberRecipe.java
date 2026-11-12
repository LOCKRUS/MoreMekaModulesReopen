package moremekasuitmodules.common.recipe;

import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A single item-processing recipe for the formed radiation chamber.
 * Fuel is charged once per active game tick.
 */
public record RadiationChamberRecipe(
        ResourceLocation id,
        Item inputItem,
        Item outputItem,
        int durationTicks,
        long fuelPerTick
) {
    public RadiationChamberRecipe {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(inputItem, "inputItem");
        Objects.requireNonNull(outputItem, "outputItem");
        if (durationTicks <= 0 || durationTicks % 20 != 0) {
            throw new IllegalArgumentException("Reaction duration must be a positive whole number of seconds");
        }
        if (fuelPerTick <= 0) {
            throw new IllegalArgumentException("Fuel use per tick must be positive");
        }
    }

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && stack.is(inputItem);
    }

    public ItemStack createInputStack() {
        return new ItemStack(inputItem);
    }

    public ItemStack createOutputStack() {
        return new ItemStack(outputItem);
    }

    public long totalFuel() {
        return Math.multiplyExact((long) durationTicks, fuelPerTick);
    }

    public int durationSeconds() {
        return durationTicks / 20;
    }
}
