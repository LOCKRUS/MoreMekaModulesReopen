package moremekasuitmodules.mixin.mekanism;

import mekanism.api.math.MathUtils;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.registration.impl.DeferredChemical;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.sps.ISpsEfficiencyUpgradeHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SPSMultiblockData.class, remap = false)
public abstract class MixinSPSMultiblockData implements ISpsEfficiencyUpgradeHolder {
    @Unique
    private static final String MOREMEKA_SPS_UPGRADE_TAG = "MoreMekaSpsEfficiencyUpgrade";
    @Unique
    private ItemStack moreMekaSuitModules$spsUpgrade = ItemStack.EMPTY;

    @Override
    public ItemStack moreMekaSuitModules$getSpsUpgrade() {
        return moreMekaSuitModules$spsUpgrade.copy();
    }

    @Override
    public void moreMekaSuitModules$setSpsUpgrade(ItemStack stack) {
        moreMekaSuitModules$spsUpgrade = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        ((SPSMultiblockData) (Object) this).markDirty();
    }

    @Unique
    private boolean moreMekaSuitModules$hasSpsUpgrade() {
        return !moreMekaSuitModules$spsUpgrade.isEmpty()
                && moreMekaSuitModules$spsUpgrade.is(MekaSuitMoreModulesItem.SPS_EFFICIENCY_AMPLIFICATION_UPGRADE.get());
    }

    @Inject(method = "readUpdateTag", at = @At("TAIL"))
    private void moreMekaSuitModules$readSpsUpgrade(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider, CallbackInfo ci) {
        if (tag.contains(MOREMEKA_SPS_UPGRADE_TAG)) {
            moreMekaSuitModules$spsUpgrade = ItemStack.parse(provider, tag.getCompound(MOREMEKA_SPS_UPGRADE_TAG)).orElse(ItemStack.EMPTY);
        } else {
            moreMekaSuitModules$spsUpgrade = ItemStack.EMPTY;
        }
    }

    @Inject(method = "writeUpdateTag", at = @At("TAIL"))
    private void moreMekaSuitModules$writeSpsUpgrade(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider, CallbackInfo ci) {
        if (!moreMekaSuitModules$spsUpgrade.isEmpty()) {
            tag.put(MOREMEKA_SPS_UPGRADE_TAG, moreMekaSuitModules$spsUpgrade.save(provider));
        } else {
            tag.remove(MOREMEKA_SPS_UPGRADE_TAG);
        }
    }

    @ModifyArg(
            method = "supplyCoilEnergy",
            at = @At(value = "INVOKE", target = "Lmekanism/api/math/MathUtils;addClamped(JJ)J"),
            index = 1)
    private long moreMekaSuitModules$reduceSpsEnergy(long energy) {
        return moreMekaSuitModules$hasSpsUpgrade() ? energy / 2 : energy;
    }

    @ModifyArg(
            method = "supplyCoilEnergy",
            at = @At(value = "INVOKE", target = "Lmekanism/common/content/sps/SPSMultiblockData$CoilData;receiveEnergy(J)V"),
            index = 0)
    private long moreMekaSuitModules$reduceCoilDisplayEnergy(long energy) {
        return moreMekaSuitModules$hasSpsUpgrade() ? energy / 2 : energy;
    }

    @ModifyArg(
            method = "process",
            at = @At(value = "INVOKE", target = "Lmekanism/common/registration/impl/DeferredChemical;asStack(J)Lmekanism/api/chemical/ChemicalStack;"),
            index = 0)
    private long moreMekaSuitModules$doubleAntimatterOutput(long amount) {
        return moreMekaSuitModules$hasSpsUpgrade() ? amount * 2L : amount;
    }
}
