package moremekasuitmodules.common.integration.jade;

import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class RadiationChamberJadeProvider implements IServerDataProvider<BlockAccessor>, IBlockComponentProvider {
    public static final RadiationChamberJadeProvider INSTANCE = new RadiationChamberJadeProvider();
    private static final String FUEL_KEY = "RadiationChamberFuel";
    private static final String CAPACITY_KEY = "RadiationChamberFuelCapacity";
    private static final String PROGRESS_KEY = "RadiationChamberReactionProgress";

    private RadiationChamberJadeProvider() {
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("moremekasuitmodules", "radiation_chamber_fuel");
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof RadiationChamberBlockEntity chamber && chamber.isStructureFormed()) {
            data.putLong(FUEL_KEY, chamber.getStoredFuel());
            data.putLong(CAPACITY_KEY, RadiationChamberBlockEntity.FUEL_CAPACITY);
            int progress = chamber.getReactionProgressPercent();
            if (progress >= 0) {
                data.putInt(PROGRESS_KEY, progress);
            }
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(FUEL_KEY) || !data.contains(CAPACITY_KEY)) {
            return;
        }
        long capacity = data.getLong(CAPACITY_KEY);
        if (capacity <= 0) {
            return;
        }
        long fuel = Math.max(0, Math.min(data.getLong(FUEL_KEY), capacity));
        Component label = Component.translatable(
                "jade.moremekasuitmodules.radiation_chamber_fuel",
                fuel,
                capacity);
        float fillRatio = (float) fuel / capacity;
        IElementHelper elements = IElementHelper.get();
        tooltip.add(elements.progress(fillRatio, label, elements.progressStyle(), BoxStyle.getViewGroup(), true));

        if (data.contains(PROGRESS_KEY)) {
            int progress = Math.max(0, Math.min(100, data.getInt(PROGRESS_KEY)));
            Component progressLabel = Component.translatable(
                    "jade.moremekasuitmodules.radiation_chamber_progress",
                    progress);
            tooltip.add(elements.progress(progress / 100.0F, progressLabel,
                    elements.progressStyle(), BoxStyle.getViewGroup(), true));
        }
    }
}
