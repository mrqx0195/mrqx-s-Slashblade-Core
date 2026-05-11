package net.mrqx.sbr_core.network;

import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.capability.concentrationrank.IConcentrationRank;
import mods.flammpfeil.slashblade.registry.ComboStateRegistry;
import mods.flammpfeil.slashblade.registry.combo.ComboState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.mrqx.sbr_core.MrqxSlashBladeCore;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.client.ClientAnimations;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 持刀实体同步数据包。
 * <p>
 * 从服务端发送至客户端，携带实体的评价点数与当前连段信息，
 * 用于客户端播放对应的 VMD 动画。
 */
public record SlashEntitySyncMessage(long rawPoint, int entityId, String combo) implements CustomPacketPayload {
    public static final Type<SlashEntitySyncMessage> TYPE = new Type<>(MrqxSlashBladeCore.prefix("slash_entity_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SlashEntitySyncMessage> STREAM_CODEC = CustomPacketPayload
        .codec(SlashEntitySyncMessage::write, SlashEntitySyncMessage::new);
    
    /**
     * 从网络缓冲区反序列化。
     */
    private SlashEntitySyncMessage(RegistryFriendlyByteBuf buf) {
        this(buf.readLong(), buf.readInt(), buf.readUtf());
    }
    
    /**
     * 序列化至网络缓冲区。
     */
    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeLong(this.rawPoint);
        buf.writeInt(this.entityId);
        buf.writeUtf(this.combo);
    }
    
    @Override
    public Type<SlashEntitySyncMessage> type() {
        return TYPE;
    }
    
    /**
     * 客户端收到数据包后：更新评价点数，查找并播放对应的 VMD 动画。
     */
    public static void handle(SlashEntitySyncMessage msg, IPayloadContext ctx) {
        if (Minecraft.getInstance().level != null) {
            Entity entity = Minecraft.getInstance().level.getEntity(msg.entityId);
            if (entity instanceof ISlashBladeEntity slashBladeEntity) {
                IConcentrationRank cr = entity.getData(CapabilityConcentrationRank.RANK_POINT.get());
                long time = entity.level().getGameTime();
                IConcentrationRank.ConcentrationRanks oldRank = cr.getRank(time);
                cr.setRawRankPoint(msg.rawPoint);
                cr.setLastUpdte(time);
                if (oldRank.level < cr.getRank(time).level) {
                    cr.setLastRankRise(time);
                }
                
                ComboState state = ComboStateRegistry.REGISTRY.get(ResourceLocation.tryParse(msg.combo));
                if (state == null) {
                    return;
                }
                ResourceLocation animation = ComboState.getRegistryKey(state);
                if (animation != null) {
                    VanillaConvertedVmdAnimation vmdAnimation = ClientAnimations.ANIMATION.get(animation);
                    if (vmdAnimation != null) {
                        slashBladeEntity.setCurrentAnimation(vmdAnimation.getClone());
                    }
                }
            }
        }
    }
}
