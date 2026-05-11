package net.mrqx.sbr_core.events.handler;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.utils.InputStream;

/**
 * 实体 Tick 事件处理器。
 * <p>
 * 每个 Tick 更新对应实体的输入流时间戳，用于输入超时判定。
 */
@Mod.EventBusSubscriber
public class TickHandler {
    /**
     * 在每个实体 Tick 结束时更新输入流时间。
     */
    @SubscribeEvent
    public static void onLivingTickEvent(LivingEvent.LivingTickEvent event) {
        InputStream.tick(event.getEntity().getUUID());
    }
}
