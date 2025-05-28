package net.harrison.battleroyale.entities.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.harrison.battleroyale.Battleroyale;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class AirdropRenderer extends EntityRenderer<AirdropEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Battleroyale.MODID, "textures/entity/airdrop.png");
    private final AirdropModel<AirdropEntity> model;

    public AirdropRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.7F;
        this.model = new AirdropModel<>(context.bakeLayer(AirdropModel.LAYER_LOCATION));
    }

    @Override
    public void render(AirdropEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();


        poseStack.translate(0.0F, 0.0F, 0.0F); // 调整高度，使模型居中
        poseStack.scale(1.0F, 1.0F, 1.0F);

        // 根据实体的朝向旋转
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));


        // 渲染模型
        this.model.renderToBuffer(
            poseStack,
            buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity))),
            packedLight,
            OverlayTexture.NO_OVERLAY,
            1.0F, 1.0F, 1.0F, 1.0F
        );





        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        poseStack.popPose();

    }

    @Override
    public ResourceLocation getTextureLocation(AirdropEntity entity) {
        return TEXTURE;
    }



}
