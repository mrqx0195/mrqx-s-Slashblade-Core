package net.mrqx.sbr_core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.mrqx.sbr_core.utils.SlashBladeAttackUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Supplier;

/**
 * 持刀实体同步数据包。
 * <p>
 * 从服务端发送至客户端，携带实体的评价点数与当前连段信息，
 * 用于客户端播放对应的 VMD 动画。
 */
public class SlashEntitySyncMessage {
    public long rawPoint;
    public int entityId;
    public String combo = "";
    
    public SlashEntitySyncMessage() {
    }
    
    /**
     * 从网络缓冲区反序列化。
     */
    public static SlashEntitySyncMessage decode(FriendlyByteBuf buf) {
        SlashEntitySyncMessage msg = new SlashEntitySyncMessage();
        msg.rawPoint = buf.readLong();
        msg.entityId = buf.readInt();
        msg.combo = buf.readUtf();
        return msg;
    }
    
    /**
     * 序列化至网络缓冲区。
     */
    public static void encode(SlashEntitySyncMessage msg, FriendlyByteBuf buf) {
        buf.writeLong(msg.rawPoint);
        buf.writeInt(msg.entityId);
        buf.writeUtf(msg.combo);
    }
    
    /**
     * 客户端收到数据包后：更新评价点数，查找并播放对应的 VMD 动画。
     */
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
