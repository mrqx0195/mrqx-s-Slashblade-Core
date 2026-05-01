package net.mrqx.sbr_core.client;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.mixin.common.AccessorVmdAnimation;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientAnimations {
    public static final Map<ResourceLocation, VanillaConvertedVmdAnimation> ANIMATION = initAnimations();
    
    private static Map<ResourceLocation, VanillaConvertedVmdAnimation> initAnimations() {
        Map<ResourceLocation, VanillaConvertedVmdAnimation> map = Maps.newHashMap();
        PlayerAnimationOverrider.getInstance().getAnimation().forEach((resourceLocation, vmdAnimation) -> {
            if (vmdAnimation instanceof AccessorVmdAnimation accessor) {
                map.put(resourceLocation, new VanillaConvertedVmdAnimation(accessor.sbr_core$getLoc(),
                    accessor.sbr_core$getStart(), accessor.sbr_core$getEnd(), accessor.sbr_core$isLoop())
                    .setBlendArms(accessor.sbr_core$isBlendArms()).setBlendLegs(accessor.sbr_core$isBlendLegs()));
            }
        });
        return map;
    }
}
