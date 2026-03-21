package net.mrqx.sbr_core.mixin.common;

import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings({"AlibabaLowerCamelCaseVariableNaming", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
@Mixin(value = VmdAnimation.class, remap = false)
public interface AccessorVmdAnimation {
    @Accessor("loc")
    ResourceLocation sbr_core$getLoc();

    @Accessor("start")
    double sbr_core$getStart();

    @Accessor("end")
    double sbr_core$getEnd();

    @Accessor("loop")
    boolean sbr_core$isLoop();

    @Accessor("blendArms")
    boolean sbr_core$isBlendArms();

    @Accessor("blendLegs")
    boolean sbr_core$isBlendLegs();
}
