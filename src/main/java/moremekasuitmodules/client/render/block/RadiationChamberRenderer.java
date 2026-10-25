package moremekasuitmodules.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.common.lib.effect.BoltEffect;
import moremekasuitmodules.common.block.RadiationChamberBlock;
import moremekasuitmodules.common.block.entity.RadiationChamberBlockEntity;
import moremekasuitmodules.common.registries.MekaSuitMoreModulesItem;
import moremekasuitmodules.common.registries.MoreMekaSuitModulesBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;

public final class RadiationChamberRenderer implements BlockEntityRenderer<RadiationChamberBlockEntity> {
    private final BoltRenderer boltRenderer = new BoltRenderer();
    private final RandomSource random = RandomSource.create();

    public RadiationChamberRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RadiationChamberBlockEntity chamber, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        ItemStack contents = chamber.getContentsForRender();
        if (contents.is(MekaSuitMoreModulesItem.STRANGLET.get())) {
            poseStack.pushPose();
            poseStack.translate(0.325, 0.325, 0.325);
            poseStack.scale(0.35F, 0.35F, 0.35F);
            BlockState strangletState = MoreMekaSuitModulesBlocks.STRANGLET.get().defaultBlockState();
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(strangletState, poseStack, buffers, packedLight, packedOverlay);
            poseStack.popPose();
        } else if (!contents.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(facingYaw(chamber)));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            Minecraft.getInstance().getItemRenderer().renderStatic(contents, ItemDisplayContext.FIXED,
                    packedLight, packedOverlay, poseStack, buffers, chamber.getLevel(), 0);
            poseStack.popPose();
        }

        if (!chamber.isReactionActive() || chamber.getLevel() == null) {
            return;
        }
        long time = chamber.getLevel().getGameTime();
        for (int i = 0; i < 5; i++) {
            double angle = time * 0.19 + partialTick * 0.19 + (Math.PI * 2 * i / 5.0);
            double nextAngle = angle + 1.2 + random.nextDouble() * 0.7;
            Vec3 start = new Vec3(0.5 + Math.cos(angle) * 0.31, 0.5 + Math.sin(angle * 1.7) * 0.28,
                    0.5 + Math.sin(angle) * 0.31);
            Vec3 end = new Vec3(0.5 + Math.cos(nextAngle) * 0.31, 0.5 + Math.sin(nextAngle * 1.7) * 0.28,
                    0.5 + Math.sin(nextAngle) * 0.31);
            BoltEffect arc = new BoltEffect(BoltEffect.BoltRenderInfo.ELECTRICITY, start, end, 12)
                    .size(0.018F)
                    .lifespan(8)
                    .spawn(BoltEffect.SpawnFunction.NO_DELAY);
            boltRenderer.update(new ArcKey(chamber.getBlockPos().asLong(), i), arc, partialTick);
        }
        boltRenderer.render(partialTick, poseStack, buffers);
    }

    private static float facingYaw(RadiationChamberBlockEntity chamber) {
        return switch (chamber.getBlockState().getValue(RadiationChamberBlock.FACING)) {
            case NORTH -> 0.0F;
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
    }

    private record ArcKey(long position, int index) {
    }
}
