package net.mrqx.sbr_core.events.handler;

import net.mrqx.sbr_core.utils.InputStream;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * 实体 Tick 事件处理器。
 * <p>
 * 每个 Tick 更新对应实体的输入流时间戳，用于输入超时判定。
 */
@EventBusSubscriber
public class TickHandler {
    /**
     * 在每个实体 Tick 结束时更新输入流时间。
     */
    @SubscribeEvent
    public static void onLivingTickEvent(EntityTickEvent.Post event) {
        InputStream.tick(event.getEntity().getUUID());
    }
}
