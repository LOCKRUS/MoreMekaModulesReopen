package moremekasuitmodules.common.block;

import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Invisible, non-colliding air infection sustained by an exposed Stranglet below it. */
public final class UnstableAirBlock extends AirBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty ABSENCE_CHECKS = IntegerProperty.create("absence_checks", 0, 8);

    private static final int SOURCE_CHECK_INTERVAL = 80;
    private static final int SOURCE_VERTICAL_RANGE = 24;
    private static final int CHECKS_BEFORE_DECAY = 8;

    public UnstableAirBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(ACTIVE, false)
                .setValue(ABSENCE_CHECKS, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, ABSENCE_CHECKS);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !oldState.is(this)) {
            BlockState current = level.getBlockState(pos);
            BlockState initialized = current
                    .setValue(ACTIVE, hasExposedStrangletBelow(level, pos))
                    .setValue(ABSENCE_CHECKS, 0);
            if (!initialized.equals(current)) {
                level.setBlock(pos, initialized, Block.UPDATE_CLIENTS);
            }
            level.scheduleTick(pos, this, SOURCE_CHECK_INTERVAL);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.is(this)) {
            return;
        }

        if (hasExposedStrangletBelow(level, pos)) {
            state = state.setValue(ACTIVE, true).setValue(ABSENCE_CHECKS, 0);
        } else {
            int absentChecks = Math.min(CHECKS_BEFORE_DECAY, state.getValue(ABSENCE_CHECKS) + 1);
            state = state.setValue(ACTIVE, false).setValue(ABSENCE_CHECKS, absentChecks);
            if (absentChecks >= CHECKS_BEFORE_DECAY && random.nextBoolean()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                return;
            }
        }

        BlockState current = level.getBlockState(pos);
        if (!current.equals(state)) {
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }

        if (state.getValue(ACTIVE) && random.nextInt(4) != 0) {
            spreadFrom(level, pos);
        }
        if (level.getBlockState(pos).is(this)) {
            level.scheduleTick(pos, this, SOURCE_CHECK_INTERVAL);
        }
    }

    /** Checks this X/Z column, up to 24 blocks below, for a Stranglet whose upper face is open. */
    public static boolean hasExposedStrangletBelow(Level level, BlockPos airPos) {
        for (int distance = 1; distance <= SOURCE_VERTICAL_RANGE; distance++) {
            BlockPos sourcePos = airPos.below(distance);
            if (level.getBlockState(sourcePos).is(MoreMekaSuitModulesBlocks.STRANGLET.get())
                    && level.getBlockState(sourcePos.above()).isAir()) {
                return true;
            }
        }
        return false;
    }

    private static void spreadFrom(ServerLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos target = pos.relative(direction);
            BlockState targetState = level.getBlockState(target);
            if (targetState.isAir() && !targetState.is(MoreMekaSuitModulesBlocks.UNSTABLE_AIR.get())) {
                level.setBlock(target, MoreMekaSuitModulesBlocks.UNSTABLE_AIR.get().defaultBlockState(), Block.UPDATE_ALL);
            } else if (StrangletBlock.canCapture(level, target, targetState)) {
                level.setBlock(target, MoreMekaSuitModulesBlocks.STRANGLET.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
}
