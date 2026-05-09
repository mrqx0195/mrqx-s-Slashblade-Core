package net.mrqx.sbr_core.events.handler;

import net.mrqx.sbr_core.utils.InputStream;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class TickHandler {
    @SubscribeEvent
    public static void onLivingTickEvent(EntityTickEvent.Post event) {
        InputStream.tick(event.getEntity().getUUID());
    }
}
