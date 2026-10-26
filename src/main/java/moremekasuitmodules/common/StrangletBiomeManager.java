package moremekasuitmodules.common;

import com.mojang.serialization.Codec;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/** Tracks biome cells changed by Stranglet and restores their original biome after the infection leaves. */
public final class StrangletBiomeManager {
    public static final ResourceKey<Biome> STRANGE_BIOME = ResourceKey.create(
            Registries.BIOME,
            MoreMekaModulesReopen.rl("strange_biome"));

    private static final Codec<Map<String, String>> BIOME_MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(HashMap::new, Function.identity());
    private static final AttachmentType<Map<String, String>> ORIGINAL_BIOMES = AttachmentType
            .builder((java.util.function.Supplier<Map<String, String>>) HashMap::new)
            .serialize(BIOME_MAP_CODEC, map -> !map.isEmpty())
            .build();

    private static final int RESTORE_INTERVAL_TICKS = 600;
    private static final int RESTORE_CELLS_PER_CHUNK = 8;
    private StrangletBiomeManager() {
    }

    /** Called when a Stranglet block is placed or created by infection. */
    public static void markInfected(Level level, BlockPos blockPos) {
        if (!(level instanceof ServerLevel serverLevel) || !serverLevel.hasChunkAt(blockPos)) {
            return;
        }

        int quartX = QuartPos.fromBlock(blockPos.getX());
        int quartY = QuartPos.fromBlock(blockPos.getY());
        int quartZ = QuartPos.fromBlock(blockPos.getZ());
        BlockPos quartPos = new BlockPos(quartX, quartY, quartZ);
        LevelChunk chunk = serverLevel.getChunkAt(blockPos);
        Map<String, String> originals = chunk.getData(ORIGINAL_BIOMES);
        String cellKey = Long.toString(quartPos.asLong());
        Holder<Biome> current = serverLevel.getBiome(blockPos);

        if (current.is(STRANGE_BIOME)) {
            return;
        }

        ResourceLocation originalKey = current.unwrapKey().map(ResourceKey::location).orElse(null);
        if (originalKey == null) {
            return;
        }

        originals.putIfAbsent(cellKey, originalKey.toString());
        setBiome(serverLevel, quartX, quartY, quartZ, holder(serverLevel, STRANGE_BIOME));
        chunk.setUnsaved(true);
    }

    public static boolean isStrangeBiome(LevelReader level, BlockPos pos) {
        return level.getBiome(pos).is(STRANGE_BIOME);
    }

    /** Runs infrequently and only visits already-loaded chunks near players. */
    public static void onServerTick(ServerLevel level) {
        if (level.getGameTime() % RESTORE_INTERVAL_TICKS != 0) {
            return;
        }
        Set<Long> visited = new HashSet<>();
        for (ServerPlayer player : level.players()) {
            int centerX = player.chunkPosition().x;
            int centerZ = player.chunkPosition().z;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    int chunkX = centerX + dx;
                    int chunkZ = centerZ + dz;
                    long packed = net.minecraft.world.level.ChunkPos.asLong(chunkX, chunkZ);
                    if (!visited.add(packed)) {
                        continue;
                    }
                    LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                    if (chunk != null) {
                        restoreOrphanedCells(level, chunk);
                    }
                }
            }
        }
    }

    private static void restoreOrphanedCells(ServerLevel level, LevelChunk chunk) {
        Map<String, String> originals = chunk.getExistingData(ORIGINAL_BIOMES).orElse(null);
        if (originals == null || originals.isEmpty()) {
            return;
        }

        int restored = 0;
        boolean changed = false;
        var iterator = originals.entrySet().iterator();
        while (iterator.hasNext() && restored < RESTORE_CELLS_PER_CHUNK) {
            Map.Entry<String, String> entry = iterator.next();
            BlockPos quart = BlockPos.of(Long.parseLong(entry.getKey()));
            int quartX = quart.getX();
            int quartY = quart.getY();
            int quartZ = quart.getZ();
            if (containsStranglet(level, quartX, quartY, quartZ)) {
                continue;
            }

            ResourceLocation originalLocation = ResourceLocation.tryParse(entry.getValue());
            if (originalLocation == null) {
                iterator.remove();
                changed = true;
                continue;
            }
            ResourceKey<Biome> originalKey = ResourceKey.create(Registries.BIOME, originalLocation);
            Holder<Biome> original = level.registryAccess().registryOrThrow(Registries.BIOME)
                    .getHolder(originalKey).orElse(null);
            if (original != null) {
                setBiome(level, quartX, quartY, quartZ, original);
            } else {
                continue;
            }
            iterator.remove();
            restored++;
            changed = true;
        }

        if (changed) {
            chunk.setUnsaved(true);
        }
        if (originals.isEmpty()) {
            chunk.removeData(ORIGINAL_BIOMES);
        }
    }

    private static boolean containsStranglet(ServerLevel level, int quartX, int quartY, int quartZ) {
        int minX = quartX << 2;
        int minY = quartY << 2;
        int minZ = quartZ << 2;
        for (int x = minX; x < minX + 4; x++) {
            for (int y = minY; y < minY + 4; y++) {
                if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) {
                    continue;
                }
                for (int z = minZ; z < minZ + 4; z++) {
                    if (level.getBlockState(new BlockPos(x, y, z)).is(MoreMekaSuitModulesBlocks.STRANGLET.get())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static void setBiome(ServerLevel level, int quartX, int quartY, int quartZ, Holder<Biome> biome) {
        int sectionY = quartY >> 2;
        int sectionIndex = sectionY - level.getMinSection();
        LevelChunk chunk = level.getChunkSource().getChunkNow(quartX >> 2, quartZ >> 2);
        if (chunk == null || sectionIndex < 0 || sectionIndex >= chunk.getSections().length) {
            return;
        }
        LevelChunkSection section = chunk.getSection(sectionIndex);
        PalettedContainerRO<Holder<Biome>> readOnly = section.getBiomes();
        if (!(readOnly instanceof PalettedContainer<?> raw)) {
            return;
        }
        PalettedContainer<Holder<Biome>> biomes = (PalettedContainer<Holder<Biome>>) raw;
        biomes.set(quartX & 3, quartY & 3, quartZ & 3, biome);
        chunk.setUnsaved(true);
        level.getChunkSource().chunkMap.resendBiomesForChunks(java.util.List.of(chunk));
    }

    private static Holder<Biome> holder(ServerLevel level, ResourceKey<Biome> key) {
        return level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(key);
    }
}
