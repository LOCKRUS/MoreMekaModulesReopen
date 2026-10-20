package moremekasuitmodules.common.sps;

import java.util.function.Consumer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.warning.ISupportsWarning;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SpsUpgradeContainerSlot extends InventoryContainerSlot {
    public SpsUpgradeContainerSlot(BasicInventorySlot slot, int x, int y,
          @Nullable Consumer<ISupportsWarning<?>> warningAdder, Consumer<ItemStack> uncheckedSetter) {
        super(slot, x, y, ContainerSlotType.EXTRA, SlotOverlay.UPGRADE, warningAdder, uncheckedSetter);
    }
}
