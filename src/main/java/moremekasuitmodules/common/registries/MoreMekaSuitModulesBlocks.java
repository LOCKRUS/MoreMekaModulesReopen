package moremekasuitmodules.common.registries;

import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.block.RadiationChamberBlock;
import moremekasuitmodules.common.block.StrangletBlock;
import moremekasuitmodules.common.block.UnstableAirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public final class MoreMekaSuitModulesBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, MoreMekaModulesReopen.MODID);

    public static final DeferredHolder<Block, Block> BURNT_GRASS = BLOCKS.register(
            "burnt_grass", () -> new Block(Block.Properties.of()
                    .strength(0.6F)
                    .sound(SoundType.GRASS)
                    .mapColor(MapColor.COLOR_GRAY)));

    public static final DeferredHolder<Block, Block> CHARRED_WOOD = BLOCKS.register(
            "charred_wood", () -> new Block(Block.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.WOOD)));

    public static final DeferredHolder<Block, Block> STRANGLET = BLOCKS.register(
            "stranglet", () -> new StrangletBlock(Block.Properties.of()
                    .strength(0.8F)
                    .sound(SoundType.SLIME_BLOCK)
                    .mapColor(MapColor.COLOR_GRAY)));

    public static final DeferredHolder<Block, Block> UNSTABLE_AIR = BLOCKS.register(
            "unstable_air", () -> new UnstableAirBlock(Block.Properties.of()
                    .noCollission()
                    .noOcclusion()
                    .replaceable()
                    .randomTicks()
                    .strength(0.0F)));

    public static final DeferredHolder<Block, Block> RADIATION_CHAMBER = BLOCKS.register(
            "radiation_chamber", () -> new RadiationChamberBlock(Block.Properties.of()
                    .strength(3.0F)
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> REACTION_BLOCK = BLOCKS.register(
            "reaction_block", () -> new Block(Block.Properties.of()
                    .strength(3.0F)
                    .sound(SoundType.METAL)));

    private MoreMekaSuitModulesBlocks() {
    }
}
