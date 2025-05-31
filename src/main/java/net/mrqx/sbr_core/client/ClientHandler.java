package net.mrqx.sbr_core.client;

import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.mrqx.sbr_core.events.SlashBladePlayerAnimationRegistryEvent;
import net.mrqx.sbr_core.mixin.playeranimation.AccessorPlayerAnimationOverrider;
import org.apache.logging.log4j.util.LoaderUtil;

@Mod.EventBusSubscriber(
        value = {Dist.CLIENT},
        bus = Mod.EventBusSubscriber.Bus.MOD
)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        if (LoaderUtil.isClassAvailable("dev.kosmx.playerAnim.api.layered.AnimationStack")) {
            MinecraftForge.EVENT_BUS.post(new SlashBladePlayerAnimationRegistryEvent(((AccessorPlayerAnimationOverrider) PlayerAnimationOverrider.getInstance()).getAnimation()));
        }
    }
}
