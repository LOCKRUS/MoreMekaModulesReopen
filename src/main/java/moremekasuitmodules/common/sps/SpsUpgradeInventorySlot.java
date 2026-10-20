package moremekasuitmodules.common.sps;

import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.api.AutomationType;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import net.minecraft.world.item.ItemStack;

public class SpsUpgradeInventorySlot extends BasicInventorySlot {
    private final ISpsEfficiencyUpgradeHolder holder;

    public SpsUpgradeInventorySlot(ISpsEfficiencyUpgradeHolder holder, int x, int y) {
        super(1, (stack, automation) -> automation == AutomationType.MANUAL,
                (stack, automation) -> automation == AutomationType.MANUAL,
                stack -> stack.is(MekaSuitMoreModulesItem.SPS_EFFICIENCY_AMPLIFICATION_UPGRADE.get()),
                null, x, y);
        this.holder = holder;
        setSlotType(ContainerSlotType.EXTRA);
        setSlotOverlay(SlotOverlay.UPGRADE);
        setStackUnchecked(holder.moreMekaSuitModules$getSpsUpgrade());
    }

    @Override
    public void onContentsChanged() {
        holder.moreMekaSuitModules$setSpsUpgrade(getStack());
    }
}
