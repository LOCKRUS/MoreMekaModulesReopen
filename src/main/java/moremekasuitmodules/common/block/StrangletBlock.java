package moremekasuitmodules.common.block;

import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Living-looking spread block created by the Encased Strappel Ingot.
 *
 * Stranglet consumes ordinary terrain and structures, but does not replace
 * unbreakable blocks. Air is instead converted into invisible Unstable Air.
 */
public class StrangletBlock extends Block {
    public StrangletBlock(BlockBehaviour.Properties properties) {
        super(properties.randomTicks());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        for (Direction direction : Direction.values()) {
            BlockPos target = pos.relative(direction);
            BlockState targetState = level.getBlockState(target);
            if (targetState.isAir()) {
                level.setBlock(target, MoreMekaSuitModulesBlocks.UNSTABLE_AIR.get().defaultBlockState(), Block.UPDATE_ALL);
            } else if (canCapture(level, target, targetState)) {
                level.setBlock(target, defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static boolean canCapture(Level level, BlockPos pos, BlockState state) {
        return !state.isAir()
                && !(state.getBlock() instanceof StrangletBlock)
                && !state.is(MoreMekaSuitModulesBlocks.UNSTABLE_AIR.get())
                && state.getDestroySpeed(level, pos) >= 0.0F;
    }
}
