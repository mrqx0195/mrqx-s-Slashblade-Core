package net.mrqx.sbr_core.events.handler;

import mods.flammpfeil.slashblade.event.handler.InputCommandEvent;
import mods.flammpfeil.slashblade.util.InputCommand;
import net.mrqx.sbr_core.utils.InputStream;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.EnumSet;

@EventBusSubscriber
public class InputCommandEventHandler {
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
