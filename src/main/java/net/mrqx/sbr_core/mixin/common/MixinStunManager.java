package net.mrqx.sbr_core.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mods.flammpfeil.slashblade.ability.StunManager;
import mods.flammpfeil.slashblade.capability.mobeffect.IMobEffectState;
import net.minecraft.world.entity.LivingEntity;
import net.mrqx.sbr_core.events.StunEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StunManager.class)
public abstract class MixinStunManager {
    @WrapOperation(method = "setStun(Lnet/minecraft/world/entity/LivingEntity;J)V", at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/capability/mobeffect/IMobEffectState;setManagedStun(JJ)V", remap = false), remap = false)
    private static void injectSetStun(IMobEffectState instance, long now, long duration, Operation<Void> original,
                                      @Local(argsOnly = true) LivingEntity target) {
        StunEvent event = new StunEvent(target, duration);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            original.call(instance, now, event.getDuration());
        }
    }
}
