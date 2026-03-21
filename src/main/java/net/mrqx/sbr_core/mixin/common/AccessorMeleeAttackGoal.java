package net.mrqx.sbr_core.mixin.common;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
@Mixin(MeleeAttackGoal.class)
public interface AccessorMeleeAttackGoal {
    @Accessor("lastCanUseCheck")
    void sbr_core$setLastCanUseCheck(long lastCanUseCheck);

    @Accessor("ticksUntilNextAttack")
    void sbr_core$setTicksUntilNextAttack(int ticksUntilNextAttack);
}
