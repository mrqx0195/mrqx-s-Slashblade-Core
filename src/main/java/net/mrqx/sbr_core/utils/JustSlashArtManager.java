package net.mrqx.sbr_core.utils;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理 Just（精准）剑技的计数与冷却时间。
 * <p>
 * 为每个生物实体（UUID）维护一个连续精准释放次数计数和一个冷却计时。
 */
public class JustSlashArtManager {
    private static final Map<UUID, Integer> JUST_COUNTER = new HashMap<>();
    private static final Map<UUID, Long> JUST_COOLDOWN_COUNTER = new HashMap<>();
    
    /**
     * 增加指定实体的 Just 计数并返回最新值。
     */
    public static int addJustCount(LivingEntity livingEntity) {
        if (!JUST_COUNTER.containsKey(livingEntity.getUUID())) {
            JUST_COUNTER.put(livingEntity.getUUID(), 0);
        }
        JUST_COUNTER.put(livingEntity.getUUID(), JUST_COUNTER.get(livingEntity.getUUID()) + 1);
        return JUST_COUNTER.get(livingEntity.getUUID());
    }
    
    /**
     * 获取指定实体的当前 Just 计数。
     */
    public static int getJustCount(LivingEntity livingEntity) {
        if (!JUST_COUNTER.containsKey(livingEntity.getUUID())) {
            JUST_COUNTER.put(livingEntity.getUUID(), 0);
        }
        return JUST_COUNTER.get(livingEntity.getUUID());
    }
    
    /**
     * 重置指定实体的 Just 计数归零。
     */
    public static void resetJustCount(LivingEntity livingEntity) {
        JUST_COUNTER.put(livingEntity.getUUID(), 0);
    }
    
    /**
     * 获取指定实体的 Just 冷却时间。
     */
    public static long getJustCooldown(LivingEntity livingEntity) {
        if (!JUST_COOLDOWN_COUNTER.containsKey(livingEntity.getUUID())) {
            JUST_COOLDOWN_COUNTER.put(livingEntity.getUUID(), 0L);
        }
        return JUST_COOLDOWN_COUNTER.get(livingEntity.getUUID());
    }
    
    /**
     * 设置指定实体的 Just 冷却时间。
     */
    public static void setJustCooldown(LivingEntity livingEntity, long cooldown) {
        JUST_COOLDOWN_COUNTER.put(livingEntity.getUUID(), cooldown);
    }
}