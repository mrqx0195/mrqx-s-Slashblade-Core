package net.mrqx.sbr_core.events.handler;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.mrqx.sbr_core.events.MrqxSlashBladeEvents;

@EventBusSubscriber()
public class RefineEventHandler {
    @SubscribeEvent
    public static void refineLimitCheck(MrqxSlashBladeEvents.RefineProgressEvent event) {
        AnvilUpdateEvent oriEvent = event.getOriginalEvent();
        int refineLimit = Math.max(10, oriEvent.getRight().getEnchantmentValue());
        if (event.getRefineResult() <= refineLimit) {
            event.setRefineResult(event.getRefineResult() + 1);
        }
    }
}
