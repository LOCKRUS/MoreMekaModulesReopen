package moremekasuitmodules.common.block.entity;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.generators.common.registries.GeneratorsChemicals;
import moremekasuitmodules.common.block.RadiationChamberBlock;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlockEntities;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class RadiationChamberBlockEntity extends BlockEntity implements ILaserReceptor {
    public static final long FUEL_CAPACITY = 1_000;
    public static final int REACTION_DURATION_TICKS = 20 * 60;
    private static final String CHEMICAL_KEY = "FuelTank";
    private static final String ITEM_KEY = "ChamberContents";
    private static final String PROGRESS_KEY = "ReactionTicks";
    private static final String ACTIVE_KEY = "ReactionActive";

    private final BasicChemicalTank fuelTank = (BasicChemicalTank) BasicChemicalTank.inputModern(
            FUEL_CAPACITY,
            stack -> stack.getChemical() == GeneratorsChemicals.FUSION_FUEL.get(),
            this::onContentsChanged);
    private ItemStack contents = ItemStack.EMPTY;
    private int reactionTicks;
    private long lastLaserTick = Long.MIN_VALUE;
    private boolean reactionActive;

    public RadiationChamberBlockEntity(BlockPos pos, BlockState state) {
        super(MoreMekaSuitModulesBlockEntities.RADIATION_CHAMBER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RadiationChamberBlockEntity blockEntity) {
        blockEntity.tickServer();
    }

    private void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }
        boolean shouldRun = containsStranglet()
                && isReactionRingComplete()
                && lastLaserTick >= level.getGameTime() - 1
                && !fuelTank.isEmpty();
        if (!shouldRun) {
            if (reactionTicks != 0) {
                reactionTicks = 0;
                setChanged();
            }
            setReactionActive(false);
            return;
        }

        ChemicalStack consumed = fuelTank.extract(1, Action.EXECUTE, AutomationType.INTERNAL);
        if (consumed.isEmpty()) {
            setReactionActive(false);
            return;
        }
        reactionTicks++;
        setChanged();
        setReactionActive(true);
        if (reactionTicks > REACTION_DURATION_TICKS) {
            contents = new ItemStack(MekaSuitMoreModulesItem.SOLIPSISMIUM_296.get());
            reactionTicks = 0;
            setReactionActive(false);
            setChanged();
            syncClient();
        }
    }

    private boolean isReactionRingComplete() {
        if (level == null) {
            return false;
        }
        Direction facing = getBlockState().getValue(RadiationChamberBlock.FACING);
        Direction left = facing.getCounterClockWise();
        BlockPos up = worldPosition.above();
        BlockPos down = worldPosition.below();
        BlockPos leftPos = worldPosition.relative(left);
        BlockPos rightPos = worldPosition.relative(left.getOpposite());
        return isReactionBlock(up)
                && isReactionBlock(down)
                && isReactionBlock(leftPos)
                && isReactionBlock(rightPos)
                && isReactionBlock(up.relative(left))
                && isReactionBlock(up.relative(left.getOpposite()))
                && isReactionBlock(down.relative(left))
                && isReactionBlock(down.relative(left.getOpposite()));
    }

    private boolean isReactionBlock(BlockPos pos) {
        return level != null && level.getBlockState(pos).is(MoreMekaSuitModulesBlocks.REACTION_BLOCK.get());
    }

    private boolean containsStranglet() {
        return contents.is(MekaSuitMoreModulesItem.STRANGLET.get());
    }

    public ItemStack getContentsForRender() {
        return contents.copy();
    }

    public boolean isReactionActive() {
        return reactionActive;
    }

    public long getStoredFuel() {
        return fuelTank.getStored();
    }

    public BasicChemicalTank getFuelHandler() {
        return fuelTank;
    }

    public boolean tryInsertStranglet(Player player, ItemStack heldStack) {
        if (level == null || level.isClientSide || !contents.isEmpty() || !heldStack.is(MekaSuitMoreModulesItem.STRANGLET.get())) {
            return false;
        }
        contents = heldStack.copyWithCount(1);
        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }
        reactionTicks = 0;
        setChanged();
        syncClient();
        return true;
    }

    public ItemStack takeContents() {
        if (level == null || level.isClientSide || contents.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack extracted = contents.copy();
        contents = ItemStack.EMPTY;
        reactionTicks = 0;
        setReactionActive(false);
        setChanged();
        syncClient();
        return extracted;
    }

    private void setReactionActive(boolean active) {
        if (reactionActive != active) {
            reactionActive = active;
            syncClient();
        }
    }

    private void onContentsChanged() {
        setChanged();
    }

    private void syncClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void receiveLaserEnergy(long energy) {
        if (level != null && !level.isClientSide && energy > 0) {
            lastLaserTick = level.getGameTime();
        }
    }

    @Override
    public boolean canLasersDig() {
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(CHEMICAL_KEY, fuelTank.serializeNBT(registries));
        if (!contents.isEmpty()) {
            tag.put(ITEM_KEY, contents.save(registries));
        }
        tag.putInt(PROGRESS_KEY, reactionTicks);
        tag.putBoolean(ACTIVE_KEY, reactionActive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(CHEMICAL_KEY)) {
            fuelTank.deserializeNBT(registries, tag.getCompound(CHEMICAL_KEY));
        }
        contents = tag.contains(ITEM_KEY)
                ? ItemStack.parseOptional(registries, tag.getCompound(ITEM_KEY))
                : ItemStack.EMPTY;
        reactionTicks = Math.max(0, tag.getInt(PROGRESS_KEY));
        reactionActive = tag.getBoolean(ACTIVE_KEY);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadWithComponents(tag, registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
