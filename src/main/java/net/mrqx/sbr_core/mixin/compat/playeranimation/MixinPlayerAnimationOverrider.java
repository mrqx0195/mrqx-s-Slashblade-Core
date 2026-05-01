package net.mrqx.sbr_core.mixin.compat.playeranimation;

import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.mrqx.sbr_core.events.SlashBladePlayerAnimationRegistryEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PlayerAnimationOverrider.class)
public abstract class MixinPlayerAnimationOverrider {
    @Final
    @Shadow(remap = false)
    private Map<ResourceLocation, VmdAnimation> animation;
    
    @Inject(method = "onBladeAnimationStart(Lmods/flammpfeil/slashblade/event/BladeMotionEvent;)V", at = @At("HEAD"), remap = false)
    private void injectInit(CallbackInfo ci) {
        if (!SlashBladePlayerAnimationRegistryEvent.hasInit()) {
            SlashBladePlayerAnimationRegistryEvent.setInit();
            MinecraftForge.EVENT_BUS.post(new SlashBladePlayerAnimationRegistryEvent(animation));
        }
    }

//    @WrapOperation(method = "onBladeAnimationStart(Lmods/flammpfeil/slashblade/event/BladeMotionEvent;)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lmods/flammpfeil/slashblade/compat/playerAnim/VmdAnimation;play()V",
//            remap = false
//        ),
//        remap = false
//    )
//    private void wrapOnBladeAnimationStart(VmdAnimation instance, Operation<Void> original, @Local(name = "player") AbstractClientPlayer player) {
//        if (instance instanceof ISlashBladeAnimation slashBladeAnimation) {
//            player.getMainHandItem().getCapability(ItemSlashBlade.BLADESTATE).ifPresent(slashBladeAnimation::mrqx_s_Slashblade_Core$setSlashBladeState);
//        }
//        original.call(instance);
//    }
}
