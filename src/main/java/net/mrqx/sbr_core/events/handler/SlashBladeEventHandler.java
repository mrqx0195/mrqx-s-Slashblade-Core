package net.mrqx.sbr_core.events.handler;

import mods.flammpfeil.slashblade.capability.concentrationrank.CapabilityConcentrationRank;
import mods.flammpfeil.slashblade.event.BladeMotionEvent;
import mods.flammpfeil.slashblade.event.SlashBladeEvent;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import net.mrqx.sbr_core.network.SlashEntitySyncMessage;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class SlashBladeEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBladeMotionEventHighPriority(BladeMotionEvent event) {
        if (event.getEntity() instanceof ISlashBladeEntity slashBladeEntity && !slashBladeEntity.canUseCombo(event.getCombo())) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBladeMotionEventLowPriority(BladeMotionEvent event) {
        if (!event.isCanceled() && !event.getEntity().level().isClientSide() && event.getEntity() instanceof ISlashBladeEntity) {
            var rank = event.getEntity().getData(CapabilityConcentrationRank.RANK_POINT.get());
            SlashEntitySyncMessage msg = new SlashEntitySyncMessage(
                Math.min(rank.getRankPoint(event.getEntity().level().getGameTime()), rank.getMaxCapacity()),
                event.getEntity().getId(),
                event.getCombo().toString()
            );
            
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(event.getEntity(), msg);
        }
    }
    
    @SubscribeEvent
    public static void onSlashBladeHitEvent(SlashBladeEvent.HitEvent event) {
        if (event.getUser() instanceof ISlashBladeEntity slashBladeEntity) {
            slashBladeEntity.hitEffect(event.getTarget());
        }
    }
}
