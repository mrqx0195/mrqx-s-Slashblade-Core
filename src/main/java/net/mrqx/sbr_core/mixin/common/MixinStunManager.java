package net.mrqx.sbr_core.mixin.common;

import mods.flammpfeil.slashblade.ability.StunManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.mrqx.sbr_core.events.StunEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StunManager.class)
public abstract class MixinStunManager {
    @Inject(method = "setStun(Lnet/minecraft/world/entity/LivingEntity;J)V", at = @At("HEAD"), remap = false, cancellable = true)
    private static void injectSetStun(LivingEntity target, long duration, CallbackInfo ci) {
        StunEvent event = new StunEvent(target, duration);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
