package moremekasuitmodules.common.recipe;

import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
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
        Item failureOutputItem,
        int successChancePercent,
        int durationTicks,
        long fuelPerTick,
        boolean requiresClearDaylight
) {
    public RadiationChamberRecipe {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(inputItem, "inputItem");
        Objects.requireNonNull(outputItem, "outputItem");
        Objects.requireNonNull(failureOutputItem, "failureOutputItem");
        if (durationTicks <= 0 || durationTicks % 20 != 0) {
            throw new IllegalArgumentException("Reaction duration must be a positive whole number of seconds");
        }
        if (fuelPerTick <= 0) {
            throw new IllegalArgumentException("Fuel use per tick must be positive");
        }
        if (successChancePercent < 0 || successChancePercent > 100) {
            throw new IllegalArgumentException("Success chance must be between 0 and 100 percent");
        }
    }

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && stack.is(inputItem);
    }

    public ItemStack createInputStack() {
        return new ItemStack(inputItem);
    }

    public ItemStack createOutputStack(RandomSource random) {
        if (successChancePercent == 100 || random.nextInt(100) < successChancePercent) {
            return new ItemStack(outputItem);
        }
        return new ItemStack(failureOutputItem);
    }

    public List<ItemStack> createPossibleOutputStacks() {
        if (outputItem == failureOutputItem) {
            return List.of(new ItemStack(outputItem));
        }
        return List.of(new ItemStack(outputItem), new ItemStack(failureOutputItem));
    }

    public boolean hasMultipleOutcomes() {
        return outputItem != failureOutputItem;
    }

    public long totalFuel() {
        return Math.multiplyExact((long) durationTicks, fuelPerTick);
    }

    public int durationSeconds() {
        return durationTicks / 20;
    }
}
