package net.mrqx.sbr_core.events.handler;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.utils.InputStream;

@Mod.EventBusSubscriber
public class TickHandler {
    @SubscribeEvent
    public static void onLivingTickEvent(LivingEvent.LivingTickEvent event) {
        InputStream.tick(event.getEntity().getUUID());
    }
}
