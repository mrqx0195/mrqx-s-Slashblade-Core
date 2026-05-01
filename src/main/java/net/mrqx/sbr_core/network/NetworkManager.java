package net.mrqx.sbr_core.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.mrqx.sbr_core.MrqxSlashBladeCore;

public class NetworkManager {
    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(MrqxSlashBladeCore.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    
    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, SlashEntitySyncMessage.class, SlashEntitySyncMessage::encode, SlashEntitySyncMessage::decode,
            SlashEntitySyncMessage::handle);
    }
}
