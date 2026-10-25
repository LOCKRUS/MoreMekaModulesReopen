package moremekasuitmodules.common.integration.jade;

import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class RadiationChamberJadeProvider implements IServerDataProvider<BlockAccessor>, IBlockComponentProvider {
    public static final RadiationChamberJadeProvider INSTANCE = new RadiationChamberJadeProvider();
    private static final String FUEL_KEY = "RadiationChamberFuel";
    private static final String CAPACITY_KEY = "RadiationChamberFuelCapacity";

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
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(FUEL_KEY) || !data.contains(CAPACITY_KEY)) {
            return;
        }
        tooltip.add(Component.translatable(
                "jade.moremekasuitmodules.radiation_chamber_fuel",
                data.getLong(FUEL_KEY),
                data.getLong(CAPACITY_KEY)));
    }
}
