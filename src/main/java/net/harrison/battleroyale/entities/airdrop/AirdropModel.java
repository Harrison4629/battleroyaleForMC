package net.harrison.battleroyale.entities.airdrop;// Made with Blockbench 4.12.4
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
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "airdrop"), "main");
	private final ModelPart base;
	private final ModelPart corner;
	private final ModelPart sidedown;
	private final ModelPart sideup;
	private final ModelPart sideface1;
	private final ModelPart sideface2;
	private final ModelPart sideface3;
	private final ModelPart sideface4;
	private final ModelPart sidepack1;
	private final ModelPart sidepack2;
	private final ModelPart sidepack3;
	private final ModelPart sidepack4;
	private final ModelPart bb_main;

	public AirdropModel(ModelPart root) {
		this.base = root.getChild("base");
		this.corner = root.getChild("corner");
		this.sidedown = root.getChild("sidedown");
		this.sideup = root.getChild("sideup");
		this.sideface1 = root.getChild("sideface1");
		this.sideface2 = root.getChild("sideface2");
		this.sideface3 = root.getChild("sideface3");
		this.sideface4 = root.getChild("sideface4");
		this.sidepack1 = root.getChild("sidepack1");
		this.sidepack2 = root.getChild("sidepack2");
		this.sidepack3 = root.getChild("sidepack3");
		this.sidepack4 = root.getChild("sidepack4");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 128).addBox(9.0F, 0.0F, -17.0F, 8.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(84, 128).addBox(-17.0F, 0.0F, -17.0F, 8.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(150, 164).addBox(-17.0F, 2.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(136, 113).addBox(-17.0F, 2.0F, -17.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 164).addBox(-17.0F, 2.0F, 8.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(34, 164).addBox(9.0F, 2.0F, 8.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(68, 164).addBox(9.0F, 2.0F, -17.0F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(168, 124).addBox(9.0F, 2.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(112, 36).addBox(-5.0F, 0.0F, -17.0F, 10.0F, 2.0F, 34.0F, new CubeDeformation(0.0F))
		.texOffs(136, 20).addBox(-5.0F, 2.0F, -17.0F, 10.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(136, 92).addBox(-5.0F, 2.0F, 8.0F, 10.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(136, 103).addBox(-5.0F, 2.0F, -4.0F, 10.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-17.0F, 4.0F, -17.0F, 34.0F, 2.0F, 34.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition corner = partdefinition.addOrReplaceChild("corner", CubeListBuilder.create().texOffs(102, 164).addBox(-17.0F, 6.0F, 14.0F, 3.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = corner.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(138, 164).addBox(-17.0F, 6.0F, 14.0F, 3.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r2 = corner.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(126, 164).addBox(-17.0F, 6.0F, 14.0F, 3.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r3 = corner.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(114, 164).addBox(-17.0F, 6.0F, 14.0F, 3.0F, 30.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition sidedown = partdefinition.addOrReplaceChild("sidedown", CubeListBuilder.create().texOffs(112, 72).addBox(-14.0F, 6.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r4 = sidedown.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(112, 87).addBox(-14.0F, 6.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r5 = sidedown.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(112, 82).addBox(-14.0F, 6.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r6 = sidedown.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(112, 77).addBox(-14.0F, 6.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition sideup = partdefinition.addOrReplaceChild("sideup", CubeListBuilder.create().texOffs(136, 0).addBox(-14.0F, 33.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r7 = sideup.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(136, 15).addBox(-14.0F, 5.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 28.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition cube_r8 = sideup.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(136, 10).addBox(-14.0F, 5.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 28.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition cube_r9 = sideup.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(136, 5).addBox(-14.0F, 5.0F, -16.0F, 28.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 28.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition sideface1 = partdefinition.addOrReplaceChild("sideface1", CubeListBuilder.create().texOffs(168, 134).addBox(-15.0F, 9.0F, 4.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(174, 72).addBox(-15.0F, 9.0F, -2.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(150, 174).addBox(-15.0F, 9.0F, 10.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(160, 174).addBox(-15.0F, 9.0F, -14.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(170, 174).addBox(-15.0F, 9.0F, -8.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition sideface2 = partdefinition.addOrReplaceChild("sideface2", CubeListBuilder.create().texOffs(0, 175).addBox(-15.0F, 9.0F, 4.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(10, 175).addBox(-15.0F, 9.0F, -2.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(20, 175).addBox(-15.0F, 9.0F, 10.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(30, 175).addBox(-15.0F, 9.0F, -14.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 175).addBox(-15.0F, 9.0F, -8.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition sideface3 = partdefinition.addOrReplaceChild("sideface3", CubeListBuilder.create().texOffs(50, 175).addBox(-15.0F, 9.0F, 4.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(60, 175).addBox(-15.0F, 9.0F, -2.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(70, 175).addBox(-15.0F, 9.0F, 10.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(80, 175).addBox(-15.0F, 9.0F, -14.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(90, 175).addBox(-15.0F, 9.0F, -8.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition sideface4 = partdefinition.addOrReplaceChild("sideface4", CubeListBuilder.create().texOffs(178, 134).addBox(-15.0F, 9.0F, 4.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(180, 174).addBox(-15.0F, 9.0F, -2.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(184, 72).addBox(-15.0F, 9.0F, 10.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(188, 134).addBox(-15.0F, 9.0F, -14.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(190, 162).addBox(-15.0F, 9.0F, -8.0F, 1.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition sidepack1 = partdefinition.addOrReplaceChild("sidepack1", CubeListBuilder.create().texOffs(108, 197).addBox(12.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(170, 113).addBox(14.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 174).addBox(10.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(194, 72).addBox(8.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(112, 197).addBox(6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(198, 200).addBox(4.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(116, 197).addBox(2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(194, 83).addBox(0.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(120, 197).addBox(-2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(186, 20).addBox(-4.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(124, 197).addBox(-6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(194, 201).addBox(-8.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(128, 197).addBox(-10.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(188, 100).addBox(-12.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(132, 197).addBox(-14.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(174, 20).addBox(-17.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition sidepack2 = partdefinition.addOrReplaceChild("sidepack2", CubeListBuilder.create().texOffs(136, 197).addBox(12.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(174, 100).addBox(14.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(202, 10).addBox(10.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(194, 190).addBox(8.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(140, 197).addBox(6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(202, 19).addBox(4.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(144, 197).addBox(2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(196, 0).addBox(0.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(198, 11).addBox(-2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(188, 112).addBox(-4.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(198, 21).addBox(-6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(202, 71).addBox(-8.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(198, 72).addBox(-10.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(190, 20).addBox(-12.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(198, 82).addBox(-14.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(176, 111).addBox(-17.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition sidepack3 = partdefinition.addOrReplaceChild("sidepack3", CubeListBuilder.create().texOffs(198, 134).addBox(12.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(180, 20).addBox(14.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(202, 80).addBox(10.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(196, 94).addBox(8.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(198, 144).addBox(6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(202, 132).addBox(4.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(198, 190).addBox(2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(196, 105).addBox(0.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 0).addBox(-2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(190, 190).addBox(-4.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 31).addBox(-6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(202, 141).addBox(-8.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 41).addBox(-10.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(192, 100).addBox(-12.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 51).addBox(-14.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(180, 100).addBox(-17.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

		PartDefinition sidepack4 = partdefinition.addOrReplaceChild("sidepack4", CubeListBuilder.create().texOffs(200, 61).addBox(12.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(182, 111).addBox(14.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(148, 202).addBox(10.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(100, 197).addBox(8.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 92).addBox(6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(152, 202).addBox(4.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 102).addBox(2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(104, 197).addBox(0.0F, 25.1F, 17.01F, 2.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 112).addBox(-2.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(192, 112).addBox(-4.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 122).addBox(-6.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(156, 202).addBox(-8.0F, 27.1F, 17.01F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 154).addBox(-10.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(194, 20).addBox(-12.0F, 24.1F, 17.01F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(200, 164).addBox(-14.0F, 26.1F, 17.01F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(182, 162).addBox(-17.0F, 25.1F, 17.01F, 3.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 36).addBox(-14.0F, 6.0F, -14.0F, 28.0F, 30.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(-34, 94).addBox(-17.0F, 36.1F, -17.0F, 34.0F, 0.0F, 34.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		corner.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sidedown.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sideup.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sideface1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sideface2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sideface3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sideface4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sidepack1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sidepack2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sidepack3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		sidepack4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}