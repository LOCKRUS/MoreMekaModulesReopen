package moremekasuitmodules.common.registries;

import mekanism.common.capabilities.Capabilities;
import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.block.RadiationChamberBlock;
import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MoreMekaSuitModulesBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MoreMekaModulesReopen.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadiationChamberBlockEntity>> RADIATION_CHAMBER =
            BLOCK_ENTITIES.register("radiation_chamber", () -> BlockEntityType.Builder
                    .of(RadiationChamberBlockEntity::new, MoreMekaSuitModulesBlocks.RADIATION_CHAMBER.get())
                    .build(null));

    private MoreMekaSuitModulesBlockEntities() {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.CHEMICAL.block(), RADIATION_CHAMBER.get(), (blockEntity, side) -> {
            if (side == null) {
                return null;
            }
            Direction rear = blockEntity.getBlockState().getValue(RadiationChamberBlock.FACING).getOpposite();
            return side == rear ? blockEntity.getFuelHandler() : null;
        });
        event.registerBlockEntity(Capabilities.LASER_RECEPTOR, RADIATION_CHAMBER.get(), (blockEntity, side) -> {
            if (side == null) {
                return null;
            }
            Direction front = blockEntity.getBlockState().getValue(RadiationChamberBlock.FACING);
            return side == front ? blockEntity : null;
        });
    }
}
