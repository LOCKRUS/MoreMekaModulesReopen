package moremekasuitmodules.common.block;

import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Invisible, spreading air-like block created when Stranglet consumes air. */
public final class UnstableAirBlock extends Block {
    public UnstableAirBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        for (Direction direction : Direction.values()) {
            BlockPos target = pos.relative(direction);
            if (level.getBlockState(target).isAir()) {
                level.setBlock(target, MoreMekaSuitModulesBlocks.UNSTABLE_AIR.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
}
