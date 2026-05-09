package net.mrqx.sbr_core.mixin.common;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface AccessorMob {
    @Invoker("getAttackBoundingBox")
    AABB sbr_core$getAttackBoundingBox();
}
