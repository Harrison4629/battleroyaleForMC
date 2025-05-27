package net.harrison.battleroyale.networking.packet;

import net.harrison.battleroyale.util.PhaseData;
import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;


public class StopPhasingC2SPacket {
    
    // 构造函数 - 无需参数
    public StopPhasingC2SPacket() {

    }

    public StopPhasingC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }
    
    // 处理 - 在接收端处理数据包
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();


            EntityType.COW.spawn(level, player.blockPosition() ,MobSpawnType.COMMAND);


        });
        supplier.get().setPacketHandled(true);
    }

}