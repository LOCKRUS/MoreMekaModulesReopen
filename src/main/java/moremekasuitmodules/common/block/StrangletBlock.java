package moremekasuitmodules.common.block;

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
 * Stranglet only replaces blocks that have a finite destroy speed. This keeps
 * bedrock, portals, barriers, and other unbreakable blocks intact while still
 * allowing it to consume ordinary terrain and structures.
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
            if (canCapture(level, target, targetState)) {
                level.setBlock(target, defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static boolean canCapture(Level level, BlockPos pos, BlockState state) {
        return !state.isAir()
                && !(state.getBlock() instanceof StrangletBlock)
                && state.getDestroySpeed(level, pos) >= 0.0F;
    }
}
