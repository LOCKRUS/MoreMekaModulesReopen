package moremekasuitmodules.common.block.entity;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.BasicChemicalTank;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.generators.common.registries.GeneratorsChemicals;
import moremekasuitmodules.common.MoreMekaModulesReopen;
import moremekasuitmodules.common.block.RadiationChamberBlock;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlockEntities;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import java.util.function.Consumer;

public final class RadiationChamberBlockEntity extends BlockEntity implements ILaserReceptor {
    public static final long FUEL_CAPACITY = 1_000;
    public static final int REACTION_DURATION_TICKS = 20 * 60;
    private static final String CHEMICAL_KEY = "FuelTank";
    private static final String ITEM_KEY = "ChamberContents";
    private static final String PROGRESS_KEY = "ReactionTicks";
    private static final String ACTIVE_KEY = "ReactionActive";
    private static final String STRUCTURE_FORMED_KEY = "StructureFormed";
    private static final ResourceLocation STRUCTURE_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath(MoreMekaModulesReopen.MODID, "radiation_chamber");
    private static final DustParticleOptions FORMATION_DUST =
            new DustParticleOptions(new Vector3f(1.0F, 0.015F, 0.015F), 1.6F);
    private static Consumer<RadiationChamberBlockEntity> clientTickHandler = ignored -> {};

    private final BasicChemicalTank fuelTank = (BasicChemicalTank) BasicChemicalTank.inputModern(
            FUEL_CAPACITY,
            stack -> stack.getChemical() == GeneratorsChemicals.FUSION_FUEL.get(),
            this::onContentsChanged);
    private ItemStack contents = ItemStack.EMPTY;
    private int reactionTicks;
    private long lastLaserTick = Long.MIN_VALUE;
    private boolean reactionActive;
    private boolean structureFormed;

    public RadiationChamberBlockEntity(BlockPos pos, BlockState state) {
        super(MoreMekaSuitModulesBlockEntities.RADIATION_CHAMBER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, RadiationChamberBlockEntity blockEntity) {
        blockEntity.tickServer();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, RadiationChamberBlockEntity blockEntity) {
        if (level.isClientSide) {
            clientTickHandler.accept(blockEntity);
        }
    }

    public static void installClientTickHandler(Consumer<RadiationChamberBlockEntity> handler) {
        clientTickHandler = handler;
    }

    private void tickServer() {
        if (level == null || level.isClientSide) {
            return;
        }
        updateStructureState(null);
        int requiredTicks = getReactionDurationTicks();
        boolean shouldRun = structureFormed
                && requiredTicks > 0
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
        if (reactionTicks >= requiredTicks) {
            if (contents.is(MekaSuitMoreModulesItem.STRANGLET.get())) {
                contents = new ItemStack(MekaSuitMoreModulesItem.SOLIPSISMIUM_296.get());
            }
            reactionTicks = 0;
            setReactionActive(false);
            setChanged();
            syncClient();
        }
    }

    /** Rechecks the eight-block reaction ring and updates external capability caches on transitions. */
    public void updateStructureState(@Nullable ServerPlayer completingPlayer) {
        if (level == null || level.isClientSide) {
            return;
        }
        boolean formed = isReactionRingComplete();
        if (formed == structureFormed) {
            return;
        }
        structureFormed = formed;
        if (!formed) {
            lastLaserTick = Long.MIN_VALUE;
        }
        setChanged();
        invalidateCapabilities();
        syncClient();
        if (formed) {
            spawnFormationParticles();
            if (completingPlayer != null) {
                awardStructureAdvancement(completingPlayer);
            }
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

    private void spawnFormationParticles() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Direction facing = getBlockState().getValue(RadiationChamberBlock.FACING);
        Direction left = facing.getCounterClockWise();
        for (int vertical = -1; vertical <= 1; vertical++) {
            for (int horizontal = -1; horizontal <= 1; horizontal++) {
                if (vertical == 0 && horizontal == 0) {
                    continue;
                }
                BlockPos reactionBlockPos = worldPosition.relative(Direction.UP, vertical).relative(left, horizontal);
                for (Direction face : new Direction[]{facing, facing.getOpposite()}) {
                    double x = reactionBlockPos.getX() + 0.5D + face.getStepX() * 0.65D;
                    double y = reactionBlockPos.getY() + 0.5D;
                    double z = reactionBlockPos.getZ() + 0.5D + face.getStepZ() * 0.65D;
                    double spreadX = face.getAxis() == Direction.Axis.X ? 0.025D : 0.22D;
                    double spreadZ = face.getAxis() == Direction.Axis.Z ? 0.025D : 0.22D;
                    serverLevel.sendParticles(FORMATION_DUST, x, y, z, 12, spreadX, 0.22D, spreadZ, 0.02D);
                }
            }
        }
    }

    private void awardStructureAdvancement(ServerPlayer player) {
        var server = player.getServer();
        if (server == null) {
            return;
        }
        var advancement = server.getAdvancements().get(STRUCTURE_ADVANCEMENT);
        if (advancement != null) {
            player.getAdvancements().award(advancement, "structure_formed");
        }
    }

    public static boolean isSupportedInput(ItemStack stack) {
        return stack.is(MekaSuitMoreModulesItem.STRANGLET.get());
    }

    private int getReactionDurationTicks() {
        if (contents.is(MekaSuitMoreModulesItem.STRANGLET.get())) {
            return REACTION_DURATION_TICKS;
        }
        return 0;
    }

    public ItemStack getContentsForRender() {
        return contents.copy();
    }

    public boolean isReactionActive() {
        return reactionActive;
    }

    public boolean isStructureFormed() {
        return structureFormed;
    }

    public long getStoredFuel() {
        return fuelTank.getStored();
    }

    public BasicChemicalTank getFuelHandler() {
        return fuelTank;
    }

    public boolean tryInsertInput(Player player, ItemStack heldStack) {
        if (level == null || level.isClientSide || !structureFormed || !contents.isEmpty()
                || !isSupportedInput(heldStack)) {
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
        if (level == null || level.isClientSide || !structureFormed || contents.isEmpty()) {
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
        if (level != null && !level.isClientSide && structureFormed && energy > 0) {
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
        tag.putBoolean(STRUCTURE_FORMED_KEY, structureFormed);
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
        structureFormed = tag.getBoolean(STRUCTURE_FORMED_KEY);
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
