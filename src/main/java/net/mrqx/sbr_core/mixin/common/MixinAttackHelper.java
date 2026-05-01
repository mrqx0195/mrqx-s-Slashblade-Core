package net.mrqx.sbr_core.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mods.flammpfeil.slashblade.util.AttackHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttackHelper.class)
public abstract class MixinAttackHelper {
    @SuppressWarnings("EmptyMethod")
    @WrapOperation(method = "attack(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;F)V",
        at = @At(value = "INVOKE", target = "Lmods/flammpfeil/slashblade/util/AttackHelper;restoreTargetMotionIfNeeded(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)V", remap = false), remap = false)
    private static void wrapAttack(Entity target, Vec3 originalMotion, Operation<Void> original) {
        // Do Nothing
    }
}
