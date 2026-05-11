package net.mrqx.sbr_core.events.handler;

import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrqx.sbr_core.utils.InputStream;

import java.util.EnumSet;

/**
 * 按键输入事件处理器。
 * <p>
 * 监听 {@link InputCommandEvent}，将按键按下/弹起状态转换记录到 {@link InputStream} 中，
 * 供连段搓招系统查询历史输入。
 */
@Mod.EventBusSubscriber
public class InputCommandEventHandler {
    /**
     * 以最高优先级处理输入事件，将按键状态变更记录到输入流中。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInputCommandEvent(InputCommandEvent event) {
        InputStream inputStream = InputStream.getOrCreateInputStream(event.getEntity());
        EnumSet<InputCommand> old = event.getOld();
        EnumSet<InputCommand> current = event.getCurrent();
        old.forEach(command -> {
            if (!current.contains(command)) {
                inputStream.addInput(command, old, InputStream.InputType.END);
            }
        });
        current.forEach(command -> {
            if (!old.contains(command)) {
                inputStream.addInput(command, current, InputStream.InputType.START);
            }
        });
    }
}
