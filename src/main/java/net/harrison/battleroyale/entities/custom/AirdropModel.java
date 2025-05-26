package net.harrison.battleroyale.entities.custom;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class AirdropModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("modid", "airdrop"), "main");
	private final ModelPart surface;
	private final ModelPart base;
	private final ModelPart bb_main;

	public AirdropModel(ModelPart root) {
		this.surface = root.getChild("surface");
		this.base = root.getChild("base");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition surface = partdefinition.addOrReplaceChild("surface", CubeListBuilder.create().texOffs(120, 36).addBox(-15.0F, -34.2F, 15.2F, 30.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 94).addBox(-15.0F, -34.2F, -15.0F, 30.0F, 0.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition cube_r1 = surface.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(136, 56).addBox(-15.0F, -34.2F, 15.2F, 30.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r2 = surface.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(84, 130).addBox(-15.0F, -34.2F, 15.2F, 30.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r3 = surface.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(120, 46).addBox(-15.0F, -34.2F, 15.2F, 30.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(120, 94).addBox(9.0F, -2.0F, -17.0F, 8.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(0, 124).addBox(-17.0F, -2.0F, -17.0F, 8.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(154, 140).addBox(-17.0F, -4.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(152, 151).addBox(-17.0F, -4.0F, -17.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(118, 151).addBox(-17.0F, -4.0F, 8.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(84, 150).addBox(9.0F, -4.0F, 8.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(120, 140).addBox(9.0F, -4.0F, -17.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(144, 130).addBox(9.0F, -4.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(120, 0).addBox(-5.0F, -2.0F, -17.0F, 10.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(136, 66).addBox(-5.0F, -4.0F, -17.0F, 10.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(136, 77).addBox(-5.0F, -4.0F, 8.0F, 10.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(84, 140).addBox(-5.0F, -4.0F, -4.0F, 10.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 58).addBox(-17.0F, -6.0F, -17.0F, 34.0F, 2.0F, 34.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r4 = bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-15.0F, -34.0F, -15.0F, 30.0F, 28.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		surface.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}