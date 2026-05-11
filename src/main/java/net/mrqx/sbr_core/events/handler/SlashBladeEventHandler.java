package net.mrqx.sbr_core.events.handler;

import mods.flammpfeil.slashblade.capability.concentrationrank.ConcentrationRankCapabilityProvider;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.network.NetworkManager;
import net.mrqx.sbr_core.network.SlashEntitySyncMessage;

/**
 * 拔刀剑通用事件处理器。
 * <p>
 * 处理挥刀动作的合法性校验、连段同步包的发送以及命中回调。
 */
@Mod.EventBusSubscriber
public class SlashBladeEventHandler {
    /**
     * 高优先级检测：如果实体不允许使用当前连段，则取消挥刀动作。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBladeMotionEventHighPriority(BladeMotionEvent event) {
        if (event.getEntity() instanceof ISlashBladeEntity slashBladeEntity && !slashBladeEntity.canUseCombo(event.getCombo())) {
            event.setCanceled(true);
        }
    }
    
    /**
     * 低优先级处理：在服务端发送连段同步包，用于客户端动画播放。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBladeMotionEventLowPriority(BladeMotionEvent event) {
        if (!event.isCanceled() && !event.getEntity().level().isClientSide() && event.getEntity() instanceof ISlashBladeEntity) {
            event.getEntity().getCapability(ConcentrationRankCapabilityProvider.RANK_POINT)
                .ifPresent(rank -> {
                    SlashEntitySyncMessage msg = new SlashEntitySyncMessage();
                    msg.entityId = event.getEntity().getId();
                    msg.rawPoint = Math.min(rank.getRankPoint(event.getEntity().level().getGameTime()), rank.getMaxCapacity());
                    msg.combo = event.getCombo().toString();
                    
                    NetworkManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntity), msg);
                });
        }
    }
    
    /**
     * 拔刀剑命中目标时触发命中效果回调。
     */
    @SubscribeEvent
    public static void onSlashBladeHitEvent(SlashBladeEvent.HitEvent event) {
        if (event.getUser() instanceof ISlashBladeEntity slashBladeEntity) {
            slashBladeEntity.hitEffect(event.getTarget());
        }
    }
}
