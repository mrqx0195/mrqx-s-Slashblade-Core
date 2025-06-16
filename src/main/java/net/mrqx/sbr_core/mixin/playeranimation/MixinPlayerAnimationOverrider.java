package net.mrqx.sbr_core.mixin.playeranimation;

import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.mrqx.sbr_core.events.SlashBladePlayerAnimationRegistryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerAnimationOverrider.class)
public abstract class MixinPlayerAnimationOverrider {
    @Shadow
    private Map<ResourceLocation, VmdAnimation> animation;

    @Inject(method = "onBladeAnimationStart(Lmods/flammpfeil/slashblade/event/BladeMotionEvent;)V", at = @At("HEAD"))
    private void injectInit(CallbackInfo ci) {
        if (!SlashBladePlayerAnimationRegistryEvent.hasInit()) {
            SlashBladePlayerAnimationRegistryEvent.setInit();
            MinecraftForge.EVENT_BUS.post(new SlashBladePlayerAnimationRegistryEvent(animation));
        }
    }
}
