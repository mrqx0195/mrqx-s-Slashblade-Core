package net.mrqx.sbr_core.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import mods.flammpfeil.slashblade.entity.IShootable;
import mods.flammpfeil.slashblade.util.TargetSelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.mrqx.sbr_core.entity.ISlashBladeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TargetSelector.class)
public abstract class MixinTargetSelector {
    @Inject(method = "getTargettableEntitiesWithinAABB(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/AABB;D)Ljava/util/List;",
            at = @At("RETURN"), remap = false)
    private static void injectGetTargettableEntities(Level world, LivingEntity attacker, AABB aabb, double reach, CallbackInfoReturnable<List<Entity>> cir,
                                                     @Local(name = "list1") List<Entity> targetList) {
        if (attacker instanceof ISlashBladeEntity slashBladeEntity) {
            List<Entity> list = slashBladeEntity.processTargetList(world, attacker, aabb, reach, targetList).stream().distinct().toList();
            targetList.clear();
            targetList.addAll(list);
        }
    }

    /**
     * 我 TM 在写啥
     * 不要使用 method = getTargettableEntitiesWithinAABB(Lnet/minecraft/world/level/Level;DLnet/minecraft/world/entity/Entity;)Ljava/util/List;
     * 会注入不进去
     */
    @SuppressWarnings("UnresolvedLocalCapture")
    @Inject(method = "getTargettableEntitiesWithinAABB*", at = @At("RETURN"), remap = false)
    private static <E extends Entity & IShootable> void injectGetTargettableEntities2(Level world, double reach, E owner, CallbackInfoReturnable<List<Entity>> cir,
                                                                                      @Local(name = "list1") List<Entity> targetList, @Local(name = "aabb") AABB aabb) {
        if (owner.getShooter() instanceof ISlashBladeEntity slashBladeEntity && owner.getShooter() instanceof LivingEntity livingEntity) {
            List<Entity> list = slashBladeEntity.processTargetList(world, livingEntity, aabb, reach, targetList).stream().distinct().toList();
            targetList.clear();
            targetList.addAll(list);
        }
    }
}
