package net.mrqx.sbr_core.events;

import mods.flammpfeil.slashblade.compat.playerAnim.VmdAnimation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.Map;

/**
 * 拔刀剑玩家动画注册事件。
 * <p>
 * 在动画初始化时触发，允许其他模组在此事件中注册或修改
 * 连段状态到 VMD 动画的映射。
 */
public class SlashBladePlayerAnimationRegistryEvent extends Event {
    private final Map<ResourceLocation, VmdAnimation> animation;
    
    public SlashBladePlayerAnimationRegistryEvent(Map<ResourceLocation, VmdAnimation> animation) {
        this.animation = animation;
    }
    
    /**
     * 获取动画映射表（可修改）。
     */
    public Map<ResourceLocation, VmdAnimation> getAnimation() {
        return this.animation;
    }
    
    
    private static boolean hasInit = false;
    
    /**
     * 动画是否已完成初始化。
     */
    public static boolean hasInit() {
        return hasInit;
    }
    
    /**
     * 标记动画系统已初始化完成。
     */
    public static void setInit() {
        hasInit = true;
    }
}
