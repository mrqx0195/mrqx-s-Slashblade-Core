package net.mrqx.sbr_core.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 网络管理器，负责注册自定义网络数据包。
 */
@EventBusSubscriber
public class NetworkManager {
    private static final String PROTOCOL_VERSION = "1";
    
    /**
     * 注册 {@link SlashEntitySyncMessage} 的 C2S 数据包处理。
     */
    @SubscribeEvent
    public static void onRegisterPayloadHandlersEvent(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(SlashEntitySyncMessage.TYPE, SlashEntitySyncMessage.STREAM_CODEC, SlashEntitySyncMessage::handle);
    }
}
