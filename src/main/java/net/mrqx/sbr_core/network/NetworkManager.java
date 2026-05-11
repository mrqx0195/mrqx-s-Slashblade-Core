package net.mrqx.sbr_core.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.mrqx.sbr_core.MrqxSlashBladeCore;

/**
 * 网络管理器，负责注册自定义网络数据包。
 */
public class NetworkManager {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(MrqxSlashBladeCore.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    
    /**
     * 注册 {@link SlashEntitySyncMessage} 的数据包编解码与处理。
     */
    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, SlashEntitySyncMessage.class, SlashEntitySyncMessage::encode, SlashEntitySyncMessage::decode,
            SlashEntitySyncMessage::handle);
    }
}
