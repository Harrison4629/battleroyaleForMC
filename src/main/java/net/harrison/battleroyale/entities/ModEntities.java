package net.harrison.battleroyale.entities;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.entities.airdrop.AirdropEntity;
import net.harrison.battleroyale.entities.airdrop.AirdropModel;
import net.harrison.battleroyale.entities.airdrop.AirdropRenderer;
import net.harrison.battleroyale.entities.liftdevice.LiftDeviceEntity;
import net.harrison.battleroyale.entities.liftdevice.LiftDeviceModel;
import net.harrison.battleroyale.entities.liftdevice.LiftDeviceRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Battleroyale.MODID);

    public static final RegistryObject<EntityType<AirdropEntity>> AIRDROP = ENTITY_TYPES.register("airdrop",
            () -> EntityType.Builder.of(AirdropEntity::new, MobCategory.MISC)
                    .sized(2.0F, 2.2F)  // 设置碰撞箱大小
                    .clientTrackingRange(200)
                    .updateInterval(3)
                    .fireImmune()  // 使实体防火
                    .build(new ResourceLocation(Battleroyale.MODID, "airdrop").toString()));

    public static final RegistryObject<EntityType<LiftDeviceEntity>> LIFTDEVICE = ENTITY_TYPES.register("liftdevice",
            () -> EntityType.Builder.of(LiftDeviceEntity::new, MobCategory.MISC)
                    .sized(1.0F, 0.2F)
                    .clientTrackingRange(20)
                    .updateInterval(3)
                    .fireImmune()
                    .build(new ResourceLocation(Battleroyale.MODID, "liftdevice").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(AIRDROP.get(), AirdropRenderer::new);
        event.registerEntityRenderer(LIFTDEVICE.get(), LiftDeviceRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AirdropModel.LAYER_LOCATION, AirdropModel::createBodyLayer);
        event.registerLayerDefinition(LiftDeviceModel.LAYER_LOCATION, LiftDeviceModel::createBodyLayer);
    }
}
