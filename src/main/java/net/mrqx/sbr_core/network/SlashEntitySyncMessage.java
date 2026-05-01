package net.mrqx.sbr_core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Supplier;

public class SlashEntitySyncMessage {
    public long rawPoint;
    public int entityId;
    public String combo = "";
    
    public SlashEntitySyncMessage() {
    }
    
    public static SlashEntitySyncMessage decode(FriendlyByteBuf buf) {
        SlashEntitySyncMessage msg = new SlashEntitySyncMessage();
        msg.rawPoint = buf.readLong();
        msg.entityId = buf.readInt();
        msg.combo = buf.readUtf();
        return msg;
    }
    
    public static void encode(SlashEntitySyncMessage msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.rawPoint);
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.combo);
    }
    
    public static void handle(SlashEntitySyncMessage msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
            return;
        }
        
        ctx.get().setPacketHandled(true);
        
        TriConsumer<Long, Integer, String> handler = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> SlashBladeAttackUtils::syncClientEntity);
        
        if (handler != null) {
            ctx.get().enqueueWork(() -> handler.accept(msg.rawPoint, msg.entityId, msg.combo));
        }
    }
}
