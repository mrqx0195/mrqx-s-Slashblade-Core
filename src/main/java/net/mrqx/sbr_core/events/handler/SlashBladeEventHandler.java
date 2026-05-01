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

@Mod.EventBusSubscriber
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
    
    @SubscribeEvent
    public static void onSlashBladeHitEvent(SlashBladeEvent.HitEvent event) {
        if (event.getUser() instanceof ISlashBladeEntity slashBladeEntity) {
            slashBladeEntity.hitEffect(event.getTarget());
        }
    }
}
