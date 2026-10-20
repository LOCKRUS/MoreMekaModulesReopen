package moremekasuitmodules.mixin.mekanism;

import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.sps.ISpsEfficiencyUpgradeHolder;
import moremekasuitmodules.common.sps.SpsUpgradeContainerSlot;
import moremekasuitmodules.common.sps.SpsUpgradeInventorySlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = mekanism.common.inventory.container.tile.MekanismTileContainer.class, remap = false)
public abstract class MixinMekanismTileContainer extends AbstractContainerMenu {
    protected MixinMekanismTileContainer() {
        super(null, 0);
    }

    @Inject(method = "addSlots", at = @At("TAIL"))
    private void moreMekaSuitModules$addSpsUpgradeSlot(CallbackInfo ci) {
        mekanism.common.inventory.container.tile.MekanismTileContainer<?> self =
                (mekanism.common.inventory.container.tile.MekanismTileContainer<?>) (Object) this;
        if (!(self.getTileEntity() instanceof TileEntitySPSCasing casing)) {
            return;
        }
        if (!(casing.getMultiblock() instanceof ISpsEfficiencyUpgradeHolder holder)) {
            return;
        }
        SpsUpgradeInventorySlot inventorySlot = new SpsUpgradeInventorySlot(holder, 177, 66);
        InventoryContainerSlot containerSlot = new SpsUpgradeContainerSlot(
                inventorySlot, 177, 66, null, inventorySlot::setStackUnchecked);
        addSlot(containerSlot);
    }
}
