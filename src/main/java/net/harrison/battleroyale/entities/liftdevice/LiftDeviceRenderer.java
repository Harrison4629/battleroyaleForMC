package net.harrison.battleroyale.entities.liftdevice;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.harrison.battleroyale.Battleroyale;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LiftDeviceRenderer extends EntityRenderer<LiftDeviceEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Battleroyale.MODID, "textures/entity/liftdevice.png");
    private final LiftDeviceModel<LiftDeviceEntity> model;

    public LiftDeviceRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.3F;
        this.model = new LiftDeviceModel<>(context.bakeLayer(LiftDeviceModel.LAYER_LOCATION));
    }

    @Override
    public void render(LiftDeviceEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

        // 渲染模型
        this.model.renderToBuffer(
                poseStack,
                buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(LiftDeviceEntity entity) {
        return TEXTURE;
    }
}
