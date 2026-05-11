package net.mrqx.sbr_core.client;

import com.google.common.collect.Maps;
import mods.flammpfeil.slashblade.compat.playerAnim.PlayerAnimationOverrider;
import net.minecraft.resources.ResourceLocation;
import net.mrqx.sbr_core.animation.VanillaConvertedVmdAnimation;
import net.mrqx.sbr_core.mixin.common.AccessorVmdAnimation;

import java.util.Map;

/**
 * 客户端动画存储。
 * <p>
 * 在初始化时从 {@link PlayerAnimationOverrider} 中读取所有 VMD 动画，
 * 转换为 {@link VanillaConvertedVmdAnimation} 存储以供查询。
 */
public class ClientAnimations {
    public static final Map<ResourceLocation, VanillaConvertedVmdAnimation> ANIMATION = initAnimations();
    
    /**
     * 初始化动画映射表：遍历已注册的动画并转换为 VanillaConvertedVmdAnimation。
     */
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
