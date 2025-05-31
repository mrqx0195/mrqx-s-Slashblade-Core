package net.mrqx.sbr_core.mixin.playeranimation;

import java.util.Map;
import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerAnimationOverrider.class)
public interface AccessorPlayerAnimationOverrider {
    @Accessor
    Map<ResourceLocation, VmdAnimation> getAnimation();
}
