package net.mrqx.sbr_core.utils;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JustSlashArtManager {
    private static final Map<UUID, Integer> JUST_COUNTER = new HashMap<>();
    private static final Map<UUID, Long> JUST_COOLDOWN_COUNTER = new HashMap<>();

    public static int addJustCount(LivingEntity livingEntity) {
        if (!JUST_COUNTER.containsKey(livingEntity.getUUID())) {
            JUST_COUNTER.put(livingEntity.getUUID(), 0);
        }
        JUST_COUNTER.put(livingEntity.getUUID(), JUST_COUNTER.get(livingEntity.getUUID()) + 1);
        return JUST_COUNTER.get(livingEntity.getUUID());
    }

    public static int getJustCount(LivingEntity livingEntity) {
        if (!JUST_COUNTER.containsKey(livingEntity.getUUID())) {
            JUST_COUNTER.put(livingEntity.getUUID(), 0);
        }
        return JUST_COUNTER.get(livingEntity.getUUID());
    }

    public static void resetJustCount(LivingEntity livingEntity) {
        JUST_COUNTER.put(livingEntity.getUUID(), 0);
    }

    public static long getJustCooldown(LivingEntity livingEntity) {
        if (!JUST_COOLDOWN_COUNTER.containsKey(livingEntity.getUUID())) {
            JUST_COOLDOWN_COUNTER.put(livingEntity.getUUID(), 0L);
        }
        return JUST_COOLDOWN_COUNTER.get(livingEntity.getUUID());
    }

    public static void setJustCooldown(LivingEntity livingEntity, long cooldown) {
        JUST_COOLDOWN_COUNTER.put(livingEntity.getUUID(), cooldown);
    }
}